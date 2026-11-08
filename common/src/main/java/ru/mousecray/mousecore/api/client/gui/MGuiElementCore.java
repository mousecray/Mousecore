/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import ru.mousecray.mouseproject.api.client.gui.component.color.MGuiColorPack;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MGuiSoundPack;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementStateManager;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiLayoutParams;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiPadding;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleType;
import ru.mousecray.mouseproject.api.client.gui.event.*;
import ru.mousecray.mouseproject.api.client.gui.misc.ClickType;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;

import javax.annotation.Nullable;
import java.util.Objects;

public class MGuiElementCore<T extends MGuiElement<T>> {
    private final GuiTickEvent<T>
            updateEvent   = new GuiTickEvent<>(),
            drawBGEvent   = new GuiTickEvent<>(),
            drawFGEvent   = new GuiTickEvent<>(),
            drawLastEvent = new GuiTickEvent<>(),
            drawTextEvent = new GuiTickEvent<>();
    private final GuiMouseClickEvent<T>
            pressEvent   = new GuiMouseClickEvent<>(ClickType.PRESS),
            releaseEvent = new GuiMouseClickEvent<>(ClickType.RELEASE),
            clickEvent   = new GuiMouseClickEvent<>(ClickType.CLICK);
    private final GuiMouseMoveEvent<T>   moveEvent   = new GuiMouseMoveEvent<>();
    private final GuiMouseDragEvent<T>   dragEvent   = new GuiMouseDragEvent<>();
    private final GuiMouseScrollEvent<T> scrollEvent = new GuiMouseScrollEvent<>();
    private final GuiKeyEvent<T>         keyEvent    = new GuiKeyEvent<>();
    private final GuiSoundEvent<T>       soundEvent  = new GuiSoundEvent<>();

    private final MGuiElementStateManager stateManager             = new MGuiElementStateManager();
    private final MutableGuiShape         shape;
    private final MutableGuiShape         calculatedShape          = new MutableGuiShape();
    private final MutableGuiShape         calculatedInnerShape     = new MutableGuiShape();
    private final MutableGuiVector        calculatedTextOffsetTemp = new MutableGuiVector();
    private final MutableGuiVector        textOffset               = new MutableGuiVector();

    private MGuiTexturePack texturePack = MGuiTexturePack.EMPTY();
    private MGuiColorPack   colorPack   = MGuiColorPack.EMPTY();
    private MGuiSoundPack   soundPack   = MGuiSoundPack.EMPTY();

    private MGuiString    guiString  = MGuiString.EMPTY();
    private GuiScaleRules scaleRules = new GuiScaleRules(GuiScaleType.FLOW);
    private GuiPadding    padding    = new GuiPadding(0);

    private int   tickDown             = -1;
    private float textScaleMultiplayer = 1.0F;

    @Nullable private MGuiScreen   screen;
    @Nullable private MGuiPanel<?> parent;
    @Nullable private FontRenderer fontRenderer;
    @Nullable private FontSize     fontSize;

    private GuiLayoutParams layoutParams = new GuiLayoutParams(null, null, null);

    public MGuiElementCore(GuiShape shape) {
        this.shape = shape.toMutable();
    }

    public void bindEvents(Minecraft mc, T th) {
        updateEvent.bind(mc, th);
        drawBGEvent.bind(mc, th);
        drawFGEvent.bind(mc, th);
        drawLastEvent.bind(mc, th);
        drawTextEvent.bind(mc, th);
        pressEvent.bind(mc, th);
        releaseEvent.bind(mc, th);
        clickEvent.bind(mc, th);
        moveEvent.bind(mc, th);
        dragEvent.bind(mc, th);
        scrollEvent.bind(mc, th);
        keyEvent.bind(mc, th);
        soundEvent.bind(mc, th);
    }

    public GuiTickEvent<T> getUpdateEvent()               { return updateEvent; }
    public GuiTickEvent<T> getDrawBGEvent()               { return drawBGEvent; }
    public GuiTickEvent<T> getDrawFGEvent()               { return drawFGEvent; }
    public GuiTickEvent<T> getDrawLastEvent()             { return drawLastEvent; }
    public GuiTickEvent<T> getDrawTextEvent()             { return drawTextEvent; }
    public GuiMouseClickEvent<T> getPressEvent()          { return pressEvent; }
    public GuiMouseClickEvent<T> getReleaseEvent()        { return releaseEvent; }
    public GuiMouseClickEvent<T> getClickEvent()          { return clickEvent; }
    public GuiMouseMoveEvent<T> getMoveEvent()            { return moveEvent; }
    public GuiMouseDragEvent<T> getDragEvent()            { return dragEvent; }
    public GuiMouseScrollEvent<T> getScrollEvent()        { return scrollEvent; }
    public GuiKeyEvent<T> getKeyEvent()                   { return keyEvent; }
    public GuiSoundEvent<T> getSoundEvent()               { return soundEvent; }

    public MGuiElementStateManager getStateManager()      { return stateManager; }
    public MutableGuiShape getShape()                     { return shape; }
    public MutableGuiShape getCalculatedShape()           { return calculatedShape; }
    public MutableGuiShape getCalculatedInnerShape()      { return calculatedInnerShape; }
    public MutableGuiVector getCalculatedTextOffsetTemp() { return calculatedTextOffsetTemp; }
    public MutableGuiVector getTextOffset()               { return textOffset; }

    public MGuiString getGuiString()                      { return guiString; }

    public void setGuiString(MGuiString guiString) {
        Objects.requireNonNull(guiString, "guiString cannot be null. Use MGuiString.EMPTY() instead.");
        this.guiString = guiString;
    }

    public MGuiTexturePack getTexturePack() { return texturePack; }

    public void setTexturePack(MGuiTexturePack texturePack) {
        Objects.requireNonNull(texturePack, "texturePack cannot be null. Use MGuiTexturePack.EMPTY() instead.");
        this.texturePack = texturePack;
    }

    public MGuiColorPack getColorPack() { return colorPack; }

    public void setColorPack(MGuiColorPack colorPack) {
        Objects.requireNonNull(colorPack, "colorPack cannot be null. Use MGuiColorPack.EMPTY() instead.");
        this.colorPack = colorPack;
    }

    public MGuiSoundPack getSoundPack() { return soundPack; }

    public void setSoundPack(MGuiSoundPack soundPack) {
        Objects.requireNonNull(soundPack, "soundPack cannot be null. Use MGuiSoundPack.EMPTY() instead.");
        this.soundPack = soundPack;
    }

    public GuiScaleRules getScaleRules() { return scaleRules; }

    public void setScaleRules(GuiScaleRules scaleRules) {
        Objects.requireNonNull(scaleRules, "scaleRules cannot be null.");
        this.scaleRules = scaleRules;
    }

    public GuiPadding getPadding() { return padding; }

    public void setPadding(GuiPadding padding) {
        Objects.requireNonNull(padding, "padding cannot be null. Use GuiPadding.DEFAULT() instead.");
        this.padding = padding;
    }

    public GuiLayoutParams getLayoutParams() { return layoutParams; }
    public void setLayoutParams(GuiLayoutParams layoutParams) {
        this.layoutParams = layoutParams != null ? layoutParams : new GuiLayoutParams(null, null, null);
    }

    public int getTickDown()                                         { return tickDown; }
    public void setTickDown(int tickDown)                            { this.tickDown = tickDown; }

    public float getTextScaleMultiplayer()                           { return textScaleMultiplayer; }
    public void setTextScaleMultiplayer(float textScaleMultiplayer)  { this.textScaleMultiplayer = textScaleMultiplayer; }

    @Nullable public MGuiScreen getScreen()                          { return screen; }
    public void setScreen(@Nullable MGuiScreen screen)               { this.screen = screen; }
    @Nullable public MGuiPanel<?> getParent()                        { return parent; }
    public void setParent(@Nullable MGuiPanel<?> parent)             { this.parent = parent; }

    @Nullable public FontRenderer getFontRenderer()                  { return fontRenderer; }
    public void setFontRenderer(@Nullable FontRenderer fontRenderer) { this.fontRenderer = fontRenderer; }
    @Nullable public FontSize getFontSize()                          { return fontSize; }
    public void setFontSize(@Nullable FontSize fontSize)             { this.fontSize = fontSize; }
}