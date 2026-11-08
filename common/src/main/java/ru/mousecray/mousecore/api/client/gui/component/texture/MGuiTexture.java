/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;

@SideOnly(Side.CLIENT)
public class MGuiTexture {
    private final ResourceLocation      texture;
    private final IGuiVector            textureSize;
    private final IGuiVector            startPos;
    private final IGuiVector            endPos;
    private final MGuiTextureScaleRules scaleRules;
    private final float                 opacity;

    private float   lastWidth  = -1;
    private float   lastHeight = -1;
    private float[] bakedQuads = new float[0];
    private int     quadCount  = 0;

    public MGuiTexture(ResourceLocation texture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize) {
        this(texture, textureSize, startPos, elementSize, new MGuiTextureScaleRules(MGuiTextureScaleType.STRETCH), 1.0f);
    }

    public MGuiTexture(ResourceLocation texture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize, MGuiTextureScaleRules scaleRules) {
        this(texture, textureSize, startPos, elementSize, scaleRules, 1.0f);
    }

    public MGuiTexture(ResourceLocation texture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize, MGuiTextureScaleRules scaleRules, float opacity) {
        this.texture = texture;
        this.textureSize = textureSize;
        this.startPos = startPos;
        endPos = elementSize;
        this.scaleRules = scaleRules != null ? scaleRules : new MGuiTextureScaleRules(MGuiTextureScaleType.STRETCH);
        this.opacity = opacity;
    }

    public ResourceLocation getTexture()         { return texture; }
    public IGuiVector getTextureSize()           { return textureSize; }
    public IGuiVector getStartPos()              { return startPos; }
    public IGuiVector getElementSize()           { return endPos; }
    public MGuiTextureScaleRules getScaleRules() { return scaleRules; }
    public float getOpacity()                    { return opacity; }

    public void bind(TextureManager manager)     { manager.bindTexture(texture); }

    public void draw(Minecraft mc, float x, float y, float width, float height) {
        if (width <= 0 || height <= 0 || opacity <= 0.001f) return;

        if (Math.abs(width - lastWidth) > 0.001f || Math.abs(height - lastHeight) > 0.001f) {
            bake(width, height);
        }

        if (quadCount == 0) return;

        bind(mc.getTextureManager());
        GlStateManager.color(1.0F, 1.0F, 1.0F, opacity);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float texW   = textureSize.x();
        float texH   = textureSize.y();
        float startX = startPos.x();
        float startY = startPos.y();

        for (int i = 0; i < quadCount; i++) {
            int idx = i * 8;
            MGuiRenderHelper.drawTexture(
                    x + bakedQuads[idx],           //drawX
                    y + bakedQuads[idx + 1],          //drawY
                    startX + bakedQuads[idx + 2],     //uvX
                    startY + bakedQuads[idx + 3],     //uvY
                    bakedQuads[idx + 4],              //uvW
                    bakedQuads[idx + 5],              //uvH
                    bakedQuads[idx + 6],              //drawW
                    bakedQuads[idx + 7],              //drawH
                    texW, texH
            );
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void bake(float width, float height) {
        lastWidth = width;
        lastHeight = height;
        quadCount = 0;

        MGuiTextureScaleRules.ScaleMode     modeX   = scaleRules.getModeX();
        MGuiTextureScaleRules.ScaleMode     modeY   = scaleRules.getModeY();
        MGuiTextureScaleRules.TextureAnchor anchorX = scaleRules.getAnchorX();
        MGuiTextureScaleRules.TextureAnchor anchorY = scaleRules.getAnchorY();

        float texW  = endPos.x();
        float texH  = endPos.y();
        float multX = scaleRules.getMultiplierX();
        float multY = scaleRules.getMultiplierY();
        if (multX == 0) multX = 1f;
        if (multY == 0) multY = 1f;

        float scaledTexW = texW * multX;
        float scaledTexH = texH * multY;
        float uvRatioX   = 1.0f / multX;
        float uvRatioY   = 1.0f / multY;

        int stepsX            = (modeX == MGuiTextureScaleRules.ScaleMode.FILL && scaledTexW > 0) ? (int) Math.ceil(width / (scaledTexW + scaleRules.getGapX())) : 1;
        int stepsY            = (modeY == MGuiTextureScaleRules.ScaleMode.FILL && scaledTexH > 0) ? (int) Math.ceil(height / (scaledTexH + scaleRules.getGapY())) : 1;
        int requiredArraySize = stepsX * stepsY * 8;

        if (bakedQuads.length < requiredArraySize) {
            bakedQuads = new float[Math.max(requiredArraySize, bakedQuads.length * 2)];
        }

        float xCursor = 0;
        while (xCursor < width) {
            float drawX = xCursor, drawW = 0, uvX = 0, uvW = 0;

            if (modeX == MGuiTextureScaleRules.ScaleMode.STRETCH) {
                drawW = width;
                uvW = texW;
                xCursor = width;
            } else if (modeX == MGuiTextureScaleRules.ScaleMode.FILL) {
                drawW = Math.min(scaledTexW, width - xCursor);
                uvW = drawW * uvRatioX;
                xCursor += scaledTexW + scaleRules.getGapX();
            } else {
                float boxStart = 0;
                if (anchorX == MGuiTextureScaleRules.TextureAnchor.CENTER) boxStart = (width - scaledTexW) / 2f;
                else if (anchorX == MGuiTextureScaleRules.TextureAnchor.MAX) boxStart = width - scaledTexW;

                float dStart = Math.max(0, boxStart);
                float dEnd   = Math.min(width, boxStart + scaledTexW);

                if (dEnd > dStart) {
                    drawX = dStart;
                    drawW = dEnd - dStart;
                    uvX = (dStart - boxStart) * uvRatioX;
                    uvW = drawW * uvRatioX;
                }
                xCursor = width;
            }

            if (drawW > 0) {
                float yCursor = 0;
                while (yCursor < height) {
                    float drawY = yCursor, drawH = 0, uvY = 0, uvH = 0;

                    if (modeY == MGuiTextureScaleRules.ScaleMode.STRETCH) {
                        drawH = height;
                        uvH = texH;
                        yCursor = height;
                    } else if (modeY == MGuiTextureScaleRules.ScaleMode.FILL) {
                        drawH = Math.min(scaledTexH, height - yCursor);
                        uvH = drawH * uvRatioY;
                        yCursor += scaledTexH + scaleRules.getGapY();
                    } else {
                        float boxStart = 0;
                        if (anchorY == MGuiTextureScaleRules.TextureAnchor.CENTER) boxStart = (height - scaledTexH) / 2f;
                        else if (anchorY == MGuiTextureScaleRules.TextureAnchor.MAX) boxStart = height - scaledTexH;

                        float dStart = Math.max(0, boxStart);
                        float dEnd   = Math.min(height, boxStart + scaledTexH);

                        if (dEnd > dStart) {
                            drawY = dStart;
                            drawH = dEnd - dStart;
                            uvY = (dStart - boxStart) * uvRatioY;
                            uvH = drawH * uvRatioY;
                        }
                        yCursor = height;
                    }

                    if (drawH > 0) {
                        int idx = quadCount * 8;
                        bakedQuads[idx] = drawX;
                        bakedQuads[idx + 1] = drawY;
                        bakedQuads[idx + 2] = uvX;
                        bakedQuads[idx + 3] = uvY;
                        bakedQuads[idx + 4] = uvW;
                        bakedQuads[idx + 5] = uvH;
                        bakedQuads[idx + 6] = drawW;
                        bakedQuads[idx + 7] = drawH;
                        quadCount++;
                    }
                }
            }
        }
    }
}