/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiMargin;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiPadding;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MGuiRenderHelper {
    private static final Vec3d LIGHT0_POS = (new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
    private static final Vec3d LIGHT1_POS = (new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();
    public static void drawTexture(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float         f             = 1.0F / tileWidth;
        float         f1            = 1.0F / tileHeight;
        Tessellator   tessellator   = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + uWidth) * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex((u + uWidth) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawCenteredString(FontRenderer fontRendererIn, String text, float x, float y, int color, boolean dropShadow) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2f, y, color, dropShadow);
    }

    public static float drawString(FontRenderer fontRendererIn, String text, float x, float y, int color, boolean dropShadow) {
        return fontRendererIn.drawString(text, x, y, color, dropShadow);
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float         f3            = (float) (color >> 24 & 255) / 255.0F;
        float         f             = (float) (color >> 16 & 255) / 255.0F;
        float         f1            = (float) (color >> 8 & 255) / 255.0F;
        float         f2            = (float) (color & 255) / 255.0F;
        Tessellator   tessellator   = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void enableBrightItemLighting() {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        GlStateManager.glLight(16384, 4611, setColorBuffer(LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0D));
        GlStateManager.glLight(16384, 4609, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
        GlStateManager.glLight(16384, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16384, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4611, setColorBuffer(LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0D));
        GlStateManager.glLight(16385, 4609, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
        GlStateManager.glLight(16385, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.shadeModel(7424);
        GlStateManager.glLightModel(2899, setColorBuffer(0.8F, 0.8F, 0.8F, 1.0F));
    }
    public static void disableBrightItemLighting() {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }

    private static FloatBuffer setColorBuffer(double r, double g, double b, double a) {
        return RenderHelper.setColorBuffer((float) r, (float) g, (float) b, (float) a);
    }

    public static void drawGradientRect(int zLevel, float left, float top, float right, float bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed   = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue  = (float) (startColor & 255) / 255.0F;
        float endAlpha   = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed     = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen   = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue    = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator   tessellator = Tessellator.getInstance();
        BufferBuilder buffer      = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTooltip(String line, Minecraft mc, int mouseX, int mouseY, FontSize fontSize, ScaledResolution sc) {
        if (line != null && !line.isEmpty()) {
            float scale        = fontSize.getScale();
            float inverseScale = 1.0F / scale;

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableDepth();

            GlStateManager.scale(scale, scale, 1.0F);
            FontRenderer font = mc.fontRenderer;

            mouseX = (int) (mouseX * inverseScale);
            mouseY = (int) (mouseY * inverseScale);

            int tooltipX = mouseX + (int) (12 * scale);
            int tooltipY = mouseY - (int) (3 * scale);

            float tooltipTextWidth = Math.max(0, font.getStringWidth(line));
            float tooltipHeight    = font.FONT_HEIGHT;

            int backgroundColor  = 0xF0100010;
            int borderColorStart = 0x505000FF;
            int borderColorEnd   = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            int zLevel           = 300;

            float borderSize  = 3;
            float borderSize2 = 4;


            float screenW = sc.getScaledWidth() * inverseScale;
            float screenH = sc.getScaledHeight() * inverseScale;

            if (tooltipX + tooltipTextWidth + borderSize > screenW) tooltipX = (int) (screenW - tooltipTextWidth - borderSize);
            if (tooltipX - borderSize2 < 0) tooltipX = (int) borderSize2;
            if (tooltipY + tooltipHeight + borderSize > screenH) tooltipY = (int) (screenH - tooltipHeight - borderSize);
            if (tooltipY - borderSize2 < 0) tooltipY = (int) borderSize2;

            //Верхняя тёмная линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize,
                    tooltipY - borderSize2,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY - borderSize,
                    backgroundColor, backgroundColor
            );
            //Нижняя тёмная линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize,
                    tooltipY + tooltipHeight + borderSize,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY + tooltipHeight + borderSize2,
                    backgroundColor, backgroundColor
            );
            //Левая тёмная линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize2,
                    tooltipY - borderSize,
                    tooltipX - borderSize,
                    tooltipY + tooltipHeight + borderSize,
                    backgroundColor, backgroundColor
            );
            //Правая тёмная линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY - borderSize,
                    tooltipX + tooltipTextWidth + borderSize2,
                    tooltipY + tooltipHeight + borderSize,
                    backgroundColor, backgroundColor
            );
            //Прямоугольник фона
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize,
                    tooltipY - borderSize,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY + tooltipHeight + borderSize,
                    backgroundColor, backgroundColor
            );
            //Верхняя светлая линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize,
                    tooltipY - borderSize,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY - borderSize + 1,
                    borderColorStart, borderColorStart
            );
            //Нижняя светлая линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize,
                    tooltipY + tooltipHeight + borderSize - 1,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY + tooltipHeight + borderSize,
                    borderColorEnd, borderColorEnd
            );
            //Левая светлая линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX - borderSize,
                    tooltipY - borderSize + 1,
                    tooltipX - borderSize + 1,
                    tooltipY + tooltipHeight + borderSize - 1,
                    borderColorStart, borderColorEnd
            );
            //Правая светлая линия
            MGuiRenderHelper.drawGradientRect(
                    zLevel,
                    tooltipX + tooltipTextWidth + borderSize - 1,
                    tooltipY - borderSize + 1,
                    tooltipX + tooltipTextWidth + borderSize,
                    tooltipY + tooltipHeight + borderSize - 1,
                    borderColorStart, borderColorEnd
            );

            MGuiRenderHelper.drawString(font, line, tooltipX, tooltipY, -1, true);

            GlStateManager.enableDepth();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static float calculateFlowComponentX(IGuiVector parentDefaultSize, IGuiVector parentContentSize, float childPixel) {
        return parentContentSize.x() * childPixel / parentDefaultSize.x();
    }

    public static float calculateFlowComponentY(IGuiVector parentDefaultSize, IGuiVector parentContentSize, float childPixel) {
        return parentContentSize.y() * childPixel / parentDefaultSize.y();
    }

    public static void calculateFlowComponentVector(
            MutableGuiVector target,
            IGuiVector parentDefaultSize, IGuiVector parentContentSize, IGuiVector childSize
    ) {
        target.withX(calculateFlowComponentX(parentDefaultSize, parentContentSize, childSize.x()));
        target.withY(calculateFlowComponentY(parentDefaultSize, parentContentSize, childSize.y()));
    }

    public static void calculateFlowComponentShape(
            MutableGuiShape target,
            IGuiVector parentDefault, IGuiVector parentCurrent, IGuiShape childTemplate, GuiScaleRules rules, IGuiShape available
    ) {
        if (rules.isFixed()) {
            target.withShape(childTemplate);
            return;
        }
        if (rules.isParent()) {
            target.withShape(available);
            return;
        }

        target.withX(childTemplate.x());
        target.withY(childTemplate.y());
        target.withWidth(childTemplate.width());
        target.withHeight(childTemplate.height());

        calculateFlowComponentVector(target.size(), parentDefault, parentCurrent, target.size());

        if (rules.isParentHorizontal()) target.withWidth(available.width());
        if (rules.isParentVertical()) target.withHeight(available.height());

        float aspect = childTemplate.height() > 0 ? childTemplate.width() / childTemplate.height() : 1f;
        if (rules.isOriginVertical()) target.withWidth(target.height() * aspect);
        else if (rules.isOriginHorizontal()) target.withHeight(target.width() / aspect);

        if (rules.isFixedHorizontal()) target.withX(childTemplate.x()).withWidth(childTemplate.width());
        if (rules.isFixedVertical()) target.withY(childTemplate.y()).withHeight(childTemplate.height());

        target.offset(available.x(), available.y());
    }

    public static void measureChildWithMargin(
            IGuiVector parentDefaultSize, IGuiVector parentContentSize, float availW, float availH,
            MGuiElement<?> child, GuiMargin margin,
            float[] marginResult, MutableGuiVector measureResult
    ) {
        float ml = calculateFlowComponentX(parentDefaultSize, parentContentSize, margin.getLeft());
        float mt = calculateFlowComponentY(parentDefaultSize, parentContentSize, margin.getTop());
        float mr = calculateFlowComponentX(parentDefaultSize, parentContentSize, margin.getRight());
        float mb = calculateFlowComponentY(parentDefaultSize, parentContentSize, margin.getBottom());

        if (marginResult != null && marginResult.length >= 4) {
            marginResult[0] = ml;
            marginResult[1] = mt;
            marginResult[2] = mr;
            marginResult[3] = mb;
        }

        float childAvailW = Math.max(0, availW - ml - mr);
        float childAvailH = Math.max(0, availH - mt - mb);

        child.measurePreferred(parentDefaultSize, parentContentSize, childAvailW, childAvailH, measureResult);
    }

    public static void checkFixedScaleRules(IGuiVector parentDefaultSize, IGuiVector parentContentSize, float suggestedX, float suggestedY, MutableGuiVector result, GuiScaleRules scaleRules, MutableGuiShape elementShape) {
        if (scaleRules.isParent()) {
            result.withX(suggestedX);
            result.withY(suggestedY);
        } else {
            calculateFlowComponentVector(result, parentDefaultSize, parentContentSize, result);

            if (scaleRules.isParentHorizontal()) result.withX(suggestedX);
            if (scaleRules.isParentVertical()) result.withY(suggestedY);

            float aspect = elementShape.height() > 0 ? elementShape.width() / elementShape.height() : 1f;
            if (scaleRules.isOriginVertical()) result.withX(result.y() * aspect);
            else if (scaleRules.isOriginHorizontal()) result.withY(result.x() / aspect);
        }
    }

    public static void measurePreferredWithScaleRules(IGuiVector parentDefaultSize, IGuiVector parentContentSize, float suggestedX, float suggestedY, MutableGuiVector result, MutableGuiShape elementShape, GuiScaleRules scaleRules) {
        result.withX(elementShape.width());
        result.withY(elementShape.height());

        boolean fullFixedOrParent = scaleRules.isFixed() || scaleRules.isParent();

        if (!scaleRules.isFixed()) {
            checkFixedScaleRules(parentDefaultSize, parentContentSize, suggestedX, suggestedY, result, scaleRules, elementShape);
        }

        if (!fullFixedOrParent) {
            if (scaleRules.isFixedHorizontal()) result.withX(elementShape.width());
            if (scaleRules.isFixedVertical()) result.withY(elementShape.height());
        }
    }

    public static void addPaddingToPreferred(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MutableGuiVector result, GuiPadding pad, GuiScaleRules scaleRules) {
        if (pad != null) {
            float padL = calculateFlowComponentX(parentDefaultSize, parentContentSize, pad.getLeft());
            float padR = calculateFlowComponentX(parentDefaultSize, parentContentSize, pad.getRight());
            float padT = calculateFlowComponentY(parentDefaultSize, parentContentSize, pad.getTop());
            float padB = calculateFlowComponentY(parentDefaultSize, parentContentSize, pad.getBottom());

            if (!scaleRules.isParentHorizontal() && !scaleRules.isFixedHorizontal()) {
                result.withX(result.x() + padL + padR);
            }
            if (!scaleRules.isParentVertical() && !scaleRules.isFixedVertical()) {
                result.withY(result.y() + padT + padB);
            }
        }
    }

    public static class ScissorState {
        public boolean enabled;
        public int     x, y, width, height;
    }

    private static final int       MAX_SCISSOR_DEPTH = 32;
    private static final int[]     scissorStackX     = new int[MAX_SCISSOR_DEPTH];
    private static final int[]     scissorStackY     = new int[MAX_SCISSOR_DEPTH];
    private static final int[]     scissorStackW     = new int[MAX_SCISSOR_DEPTH];
    private static final int[]     scissorStackH     = new int[MAX_SCISSOR_DEPTH];
    private static       int       scissorDepth      = 0;
    private static final IntBuffer SCISSOR_BUFFER    = BufferUtils.createIntBuffer(16);

    private static boolean frameScissorCached = false;
    private static int     cachedBaseX, cachedBaseY, cachedBaseW, cachedBaseH;

    public static void beginFrame() {
        scissorDepth = 0;
        frameScissorCached = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        if (frameScissorCached) {
            SCISSOR_BUFFER.clear();
            GL11.glGetInteger(GL11.GL_SCISSOR_BOX, SCISSOR_BUFFER);
            cachedBaseX = SCISSOR_BUFFER.get(0);
            cachedBaseY = SCISSOR_BUFFER.get(1);
            cachedBaseW = SCISSOR_BUFFER.get(2);
            cachedBaseH = SCISSOR_BUFFER.get(3);
        }
    }

    public static void pushScissor(int x, int y, int width, int height, ScissorState state) {
        state.enabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

        if (state.enabled && scissorDepth == 0) {
            if (frameScissorCached) {
                scissorStackX[0] = cachedBaseX;
                scissorStackY[0] = cachedBaseY;
                scissorStackW[0] = cachedBaseW;
                scissorStackH[0] = cachedBaseH;
            } else {
                Minecraft mc = Minecraft.getMinecraft();
                scissorStackX[0] = 0;
                scissorStackY[0] = 0;
                scissorStackW[0] = mc.displayWidth;
                scissorStackH[0] = mc.displayHeight;
            }
            scissorDepth = 1;
        }

        int targetX = x, targetY = y, targetW = width, targetH = height;

        if (scissorDepth > 0) {
            int pX = scissorStackX[scissorDepth - 1];
            int pY = scissorStackY[scissorDepth - 1];
            int pW = scissorStackW[scissorDepth - 1];
            int pH = scissorStackH[scissorDepth - 1];

            int right = Math.min(x + width, pX + pW);
            int top   = Math.min(y + height, pY + pH);
            targetX = Math.max(x, pX);
            targetY = Math.max(y, pY);
            targetW = Math.max(0, right - targetX);
            targetH = Math.max(0, top - targetY);
        }

        if (scissorDepth < MAX_SCISSOR_DEPTH) {
            scissorStackX[scissorDepth] = targetX;
            scissorStackY[scissorDepth] = targetY;
            scissorStackW[scissorDepth] = targetW;
            scissorStackH[scissorDepth] = targetH;
            scissorDepth++;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(targetX, targetY, targetW, targetH);
    }

    public static void popScissor(ScissorState state) {
        if (scissorDepth > 0) scissorDepth--;

        if (scissorDepth > 0) {
            GL11.glScissor(
                    scissorStackX[scissorDepth - 1],
                    scissorStackY[scissorDepth - 1],
                    scissorStackW[scissorDepth - 1],
                    scissorStackH[scissorDepth - 1]
            );
        } else if (!state.enabled) GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}