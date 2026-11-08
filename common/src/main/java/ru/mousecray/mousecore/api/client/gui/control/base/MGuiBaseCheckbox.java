/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control.base;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexture;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.event.GuiTickEvent;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleType.ORIGIN_VERTICAL;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public abstract class MGuiBaseCheckbox<T extends MGuiBaseCheckbox<T>> extends MGuiSelectableButton<T> {
    private final float boxOriginalWidth;

    public MGuiBaseCheckbox(GuiShape shape, MGuiString text, FontRenderer fontRenderer) {
        super(shape, text);
        setFontRenderer(fontRenderer);
        setShape(new GuiShape(
                shape.x(),
                shape.y(),
                fontRenderer.getStringWidth(text.get()) + 2f + shape.width(),
                Math.max(shape.height(), fontRenderer.FONT_HEIGHT)
        ));
        boxOriginalWidth = shape.width();
        setScaleRules(new GuiScaleRules(ORIGIN_VERTICAL));
        setGuiString(text);
    }

    @Override
    public void onDrawBackground(GuiTickEvent<T> event) {
        List<MGuiTexture> textures        = getTexturePack().getCalculatedTextures(getStateManager());
        MutableGuiShape   calculatedShape = getCalculatedShape();
        for (MGuiTexture texture : textures) {
            float scaleY  = calculatedShape.height() / Math.max(1f, getShape().height());
            float curBoxW = boxOriginalWidth * scaleY;

            float boxX = calculatedShape.x() + calculatedShape.width() - curBoxW;
            float boxY = calculatedShape.y();

            texture.draw(event.getMc(), boxX, boxY, curBoxW, calculatedShape.height());
        }
    }

    @Override
    public void onDrawText(GuiTickEvent<T> event) {
        if (displayString != null) {
            FontRenderer fontrenderer = event.getMc().fontRenderer;
            int          color        = getColorPack().getCalculatedColor(getStateManager(), getPackedFGColour());

            float scale        = getFontSize().getScale() * getTextScaleMultiplayer();
            float inverseScale = 1.0F / scale;

            GlStateManager.pushMatrix();
            GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            GlStateManager.scale(scale, scale, 1.0F);
            MutableGuiShape  calculatedInnerShape     = getCalculatedInnerShape();
            MutableGuiVector calculatedTextOffsetTemp = getCore().getCalculatedTextOffsetTemp();
            MGuiRenderHelper.drawString(
                    fontrenderer, displayString,
                    (calculatedInnerShape.x()) * inverseScale + calculatedTextOffsetTemp.x() * inverseScale,
                    calculatedInnerShape.y() * inverseScale + calculatedInnerShape.height() * inverseScale /
                            2f - (fontrenderer.FONT_HEIGHT) / 2f + calculatedTextOffsetTemp.y() * inverseScale,
                    color, getFontSize() != FontSize.SMALL
            );
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void setGuiString(MGuiString guiString) {
        super.setGuiString(guiString);
        getShape().withWidth((float) getFontRenderer().getStringWidth(guiString.get()) + 2f + boxOriginalWidth);
    }
}
