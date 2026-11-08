/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.component.color.MGuiColorPack;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.event.GuiTickEvent;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MGuiLabel<T extends MGuiLabel<T>> extends GuiLabel implements MGuiElement<T> {
    protected final MGuiElementCore<T> core;
    private         boolean            centered;

    public MGuiLabel(GuiShape shape) {
        super(
                Minecraft.getMinecraft().fontRenderer, 0,
                (int) shape.x(), (int) shape.y(),
                (int) shape.width(), (int) shape.height(),
                14737632
        );

        core = new MGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        getStateManager().setForbidden(MGuiElementState.FOCUSED, true);
        setColorPack(MGuiColorPack.LABEL_SIMPLE());

        getStateManager().setChangeListener(() -> visible = !getStateManager().has(MGuiElementState.HIDDEN));
    }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MGuiElementCore<T> getCore()            { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setGuiString(MGuiString guiString) {
        MGuiElement.super.setGuiString(guiString);
        labels.clear();
        String[] split = guiString.get().split("\n");
        labels.addAll(Arrays.asList(split));
    }

    public void setCentered(boolean centered) { this.centered = centered; }
    public boolean isCentered()               { return centered; }

    @Override
    public void setupShapeToVanilla(IGuiShape result) {
        x = (int) result.x();
        y = (int) result.y();
        width = (int) result.width();
        height = (int) result.height();
    }

    @Override
    public void onDrawText(GuiTickEvent<T> event) {
        if (labels.isEmpty()) return;

        FontRenderer fr    = getFontRenderer();
        FontSize     fs    = getFontSize();
        int          color = getColorPack().getCalculatedColor(getStateManager());

        float scale    = fs.getScale() * getTextScaleMultiplayer();
        float invScale = 1.0F / scale;

        MutableGuiShape calculatedInnerShape = getCalculatedInnerShape();
        float           innerX               = calculatedInnerShape.x();
        float           innerY               = calculatedInnerShape.y();
        float           innerW               = calculatedInnerShape.width();
        float           innerH               = calculatedInnerShape.height();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        GlStateManager.pushMatrix();
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.scale(scale, scale, 1.0F);

        MutableGuiVector calculatedTextOffsetTemp = getCore().getCalculatedTextOffsetTemp();
        float            totalTextHeight          = labels.size() * fr.FONT_HEIGHT;
        float startY =
                (innerY + innerH / 2f) * invScale - (totalTextHeight / 2f) + calculatedTextOffsetTemp.y() * invScale;

        FontSize fontSize = getFontSize();
        for (int i = 0; i < labels.size(); i++) {
            String line  = labels.get(i);
            float  lineY = startY + (i * fr.FONT_HEIGHT);

            if (centered) {
                MGuiRenderHelper.drawCenteredString(
                        fr, line,
                        (innerX + innerW / 2f) * invScale + calculatedTextOffsetTemp.x() * invScale,
                        lineY,
                        color,
                        fs != FontSize.SMALL
                );
            } else {
                MGuiRenderHelper.drawString(
                        fr, line,
                        innerX * invScale + calculatedTextOffsetTemp.x() * invScale,
                        lineY,
                        color, fontSize != FontSize.SMALL
                );
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    //Интеграция с vanilla
    @Override
    public boolean mouseHover(Minecraft mc, int mouseX, int mouseY) {
        return MGuiElement.super.mouseHover(mc, mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return MGuiElement.super.mousePressed(mc, mouseX, mouseY);
    }

    @Override public final int getHoverState(boolean mouseOver)           { return MGuiElement.super.getHoverState(mouseOver); }
    @Override public void mouseReleased(int mouseX, int mouseY)           { MGuiElement.super.mouseReleased(mouseX, mouseY); }
    @Override public final void playPressSound(SoundHandler soundHandler) { MGuiElement.super.playPressSound(soundHandler); }
    @Override public boolean isMouseOver()                                { return MGuiElement.super.isMouseOver(); }

    @Override
    public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
        dispatchDraw(mc, mouseX, mouseY, mc.getRenderPartialTicks());
    }

    @Override
    protected void drawLabelBackground(Minecraft mc, int mouseX, int mouseY) {
        dispatchDrawBackground(mc, mouseX, mouseY, mc.getRenderPartialTicks());
    }

    @Override
    public GuiLabel setCentered() {
        setCentered(true);
        return this;
    }
}
