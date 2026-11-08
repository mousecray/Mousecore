/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.component.color.MGuiColorPack;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MGuiSoundPack;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementStateManager;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexture;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiPadding;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.event.*;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;
import ru.mousecray.mouseproject.api.client.gui.misc.MoveDirection;
import ru.mousecray.mouseproject.api.client.gui.misc.ScrollDirection;
import ru.mousecray.mouseproject.core.MouseProject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper.*;
import static ru.mousecray.mouseproject.api.client.gui.event.GuiEventFactory.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface MGuiElement<T extends MGuiElement<T>> {
    T self();
    MGuiElementCore<T> getCore();

    //Идентификация и иерархия
    void setId(int id);
    int getId();

    default void setScreen(@Nullable MGuiScreen screen) {
        getCore().setScreen(screen);
        getStateManager().lockForbidden(screen != null || getParent() != null);
    }

    @Nullable default MGuiScreen getScreen() { return getCore().getScreen(); }

    default void setParent(@Nullable MGuiPanel<?> parent) {
        getCore().setParent(parent);
        getStateManager().lockForbidden(parent != null || getScreen() != null);
    }

    @Nullable default MGuiPanel<?> getParent() { return getCore().getParent(); }

    //Данные и состояние
    default String getText() { return getGuiString().get(); }
    default void setText(String text)                        { setGuiString(MGuiString.simple(text)); }
    default MGuiString getGuiString()                        { return getCore().getGuiString(); }
    default void setGuiString(MGuiString guiString)          { getCore().setGuiString(guiString); }

    default boolean isVisible()                              { return !getStateManager().has(MGuiElementState.HIDDEN); }
    default boolean isEnabled()                              { return !getStateManager().has(MGuiElementState.DISABLED); }
    default boolean isHovered()                              { return getStateManager().has(MGuiElementState.HOVERED); }
    default boolean isFocused()                              { return getStateManager().has(MGuiElementState.FOCUSED); }
    default boolean canBeFocused()                           { return !getStateManager().isForbidden(MGuiElementState.FOCUSED); }

    default MGuiElementStateManager getStateManager()        { return getCore().getStateManager(); }

    default MGuiTexturePack getTexturePack()                 { return getCore().getTexturePack(); }
    default void setTexturePack(MGuiTexturePack texturePack) { getCore().setTexturePack(texturePack); }
    default MGuiSoundPack getSoundPack()                     { return getCore().getSoundPack(); }
    default void setSoundPack(MGuiSoundPack soundPack)       { getCore().setSoundPack(soundPack); }
    default MGuiColorPack getColorPack()                     { return getCore().getColorPack(); }
    default void setColorPack(MGuiColorPack colorPack)       { getCore().setColorPack(colorPack); }

    default int getPackedFGColour()                          { return 0; }

    default FontRenderer getFontRenderer() {
        if (getCore().getFontRenderer() != null) return getCore().getFontRenderer();
        if (getScreen() != null) return getScreen().getFontRenderer();
        return Minecraft.getMinecraft().fontRenderer;
    }

    default void setFontRenderer(@Nullable FontRenderer fontRenderer) {
        if (getScreen() != null) {
            MouseProject.LOGGER.warn(
                    "FontRenderer cannot be setup immediately to MGuiElement that added to container." +
                            " It set now, but actual element size will be updated on the next gui size calculation."
            );
        }
        getCore().setFontRenderer(fontRenderer);
    }

    default FontSize getFontSize() {
        if (getCore().getFontSize() != null) return getCore().getFontSize();
        if (getScreen() != null) return getScreen().getFontSize();
        return FontSize.NORMAL;
    }

    default void setFontSize(@Nullable FontSize fontSize) {
        if (getScreen() != null) {
            MouseProject.LOGGER.warn(
                    "FontSize cannot be setup immediately to MGuiElement that added to container." +
                            " It set now, but actual element size will be updated on the next gui size calculation."
            );
        }
        getCore().setFontSize(fontSize);
    }

    default float getTextScaleMultiplayer()                 { return getCore().getTextScaleMultiplayer(); }
    default void setTextScaleMultiplayer(float multiplayer) { getCore().setTextScaleMultiplayer(multiplayer); }

    //Геометрия
    default void setShape(IGuiShape shape) { getShape().withShape(shape); }
    default MutableGuiShape getShape()                   { return getCore().getShape(); }
    default MutableGuiShape getCalculatedShape()         { return getCore().getCalculatedShape(); }
    default MutableGuiShape getCalculatedInnerShape()    { return getCore().getCalculatedInnerShape(); }

    default GuiScaleRules getScaleRules()                { return getCore().getScaleRules(); }
    default void setScaleRules(GuiScaleRules scaleRules) { getCore().setScaleRules(scaleRules); }
    default GuiPadding getPadding()                      { return getCore().getPadding(); }
    default void setPadding(GuiPadding padding)          { getCore().setPadding(padding); }

    default MutableGuiVector getTextOffset()             { return getCore().getTextOffset(); }
    default void setTextOffset(IGuiVector offset)        { getTextOffset().withVector(offset); }

    default void calculate(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape available) {
        MutableGuiShape calcShape = getCalculatedShape();
        GuiScaleRules   rules     = getScaleRules();

        calculateFlowComponentShape(
                calcShape, pDefSize, pContentSize, getShape(), rules, available
        );

        if (rules.isWrapHorizontal() || rules.isWrapVertical()) {
            MutableGuiVector pref = new MutableGuiVector();
            measurePreferred(pDefSize, pContentSize, available.width(), available.height(), pref);

            if (rules.isWrapHorizontal()) calcShape.withWidth(pref.x());
            if (rules.isWrapVertical()) calcShape.withHeight(pref.y());
        }

        if (calcShape.width() <= 0 || calcShape.height() <= 0) return;

        GuiPadding pad  = getPadding();
        float      padL = calculateFlowComponentX(pDefSize, pContentSize, pad.getLeft());
        float      padT = calculateFlowComponentY(pDefSize, pContentSize, pad.getTop());
        float      padR = calculateFlowComponentX(pDefSize, pContentSize, pad.getRight());
        float      padB = calculateFlowComponentY(pDefSize, pContentSize, pad.getBottom());

        MutableGuiShape calcInnerShape = getCalculatedInnerShape();
        calcInnerShape.withShape(calcShape);
        calcInnerShape.grow(-padL, -padT, -padR, -padB);

        calculateTextOffset(pDefSize, pContentSize);

        syncVanilla();

        onCalculated(pDefSize, pContentSize, calcInnerShape);
    }

    default void measurePreferred(IGuiVector pDefSize, IGuiVector pContentSize, float sugX, float sugY, MutableGuiVector result) {
        GuiScaleRules sr = getScaleRules();
        MGuiRenderHelper.measurePreferredWithScaleRules(
                pDefSize, pContentSize, sugX, sugY,
                result, getShape(), sr
        );
        MGuiRenderHelper.addPaddingToPreferred(pDefSize, pContentSize, result, getPadding(), sr);
    }

    default void offsetCalculatedShape(float dx, float dy) {
        getCalculatedShape().offset(dx, dy);
        getCalculatedInnerShape().offset(dx, dy);
        syncVanilla();
        onOffset(dx, dy);
    }

    default void calculateTextOffset(IGuiVector pDefSize, IGuiVector pContentSize) {
        MGuiRenderHelper.calculateFlowComponentVector(
                getCore().getCalculatedTextOffsetTemp(), pDefSize,
                pContentSize, getTextOffset()
        );
    }

    default void syncVanilla() {
        setupShapeToVanilla(getCalculatedShape());
    }

    default void setupShapeToVanilla(IGuiShape result)                                                { }
    default void onOffset(float dx, float dy)                                                         { }
    default void onCalculated(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape innerCalcShape) { }

    //Диспетчеризация событий
    default void dispatchUpdate(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MGuiElementCore<T> core     = getCore();
        int                tickDown = core.getTickDown();
        if (tickDown >= 0) core.setTickDown(tickDown + 1);

        GuiTickEvent<T> updateEvent = core.getUpdateEvent();
        pushTickEvent(updateEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(updateEvent);
        if (!updateEvent.isCancelled()) onUpdate(updateEvent);

        GuiMouseMoveEvent<T> moveEvent = core.getMoveEvent();
        int                  diffX     = mouseX - moveEvent.getMouseX();
        int                  diffY     = mouseY - moveEvent.getMouseY();
        MoveDirection        direction = MoveDirection.getMoveDirection(diffX, diffY);
        pushMouseMoveEvent(moveEvent, mouseX, mouseY, direction);

        if (tickDown >= 0 && direction != null) dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY);
    }

    default void dispatchProcessHover(Minecraft mc, int mouseX, int mouseY) { }

    default void dispatchMouseEnter(Minecraft mc, int mouseX, int mouseY) {
        MGuiElementCore<T>   core      = getCore();
        GuiMouseMoveEvent<T> moveEvent = core.getMoveEvent();
        core.getStateManager().add(MGuiElementState.HOVERED);
        pushMouseMoveEvent(moveEvent, mouseX, mouseY, MoveDirection.calculateMoveDirection(mouseX, mouseY, moveEvent));
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.ENTER);
            onMouseEnter(moveEvent);
        }
    }

    default void dispatchMouseLeave(Minecraft mc, int mouseX, int mouseY) {
        MGuiElementCore<T>   core      = getCore();
        GuiMouseMoveEvent<T> moveEvent = core.getMoveEvent();
        core.getStateManager().remove(MGuiElementState.HOVERED);
        pushMouseMoveEvent(moveEvent, mouseX, mouseY, MoveDirection.calculateMoveDirection(mouseX, mouseY, moveEvent));
        onAnyEventFire(moveEvent);
        if (!moveEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.LEAVE);
            onMouseLeave(moveEvent);
        }
    }

    default boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        MGuiElementCore<T> core = getCore();
        if (!core.getCalculatedShape().contains(mouseX, mouseY)) return false;
        if (!isEnabled() || !isVisible()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DISABLED);
            return false;
        }

        MGuiElementStateManager stateManager = core.getStateManager();

        if (stateManager.has(MGuiElementState.FAIL)) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.FAIL);
            return true;
        }

        //Интеграция Forge
        if (getScreen() != null && onForgeClickIntegrationPre()) return true;

        core.setTickDown(0);
        stateManager.add(MGuiElementState.PRESSED);

        GuiMouseClickEvent<T> pressEvent = core.getPressEvent();
        pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.PRESS);
            onMousePressed(pressEvent);
        }

        //Интеграция Forge
        if (getScreen() != null) onForgeClickIntegrationPost();

        return true;
    }

    default void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        MGuiElementCore<T>      core         = getCore();
        MGuiElementStateManager stateManager = core.getStateManager();

        core.setTickDown(-1);
        stateManager.remove(MGuiElementState.PRESSED);

        GuiMouseClickEvent<T> releaseEvent = core.getReleaseEvent();

        pushMouseClickEvent(releaseEvent, mouseX, mouseY);
        onAnyEventFire(releaseEvent);

        if (!releaseEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.RELEASE);
            onMouseReleased(releaseEvent);
            if (core.getCalculatedShape().contains(mouseX, mouseY)) {
                GuiMouseClickEvent<T> clickEvent = core.getClickEvent();
                pushMouseClickEvent(clickEvent, mouseX, mouseY);
                onAnyEventFire(clickEvent);
                if (!clickEvent.isCancelled()) {
                    dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.CLICK);
                    onClick(clickEvent);
                }
            }
        }
    }

    default boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MoveDirection dir, int diffX, int diffY) {
        MGuiElementCore<T>   core      = getCore();
        int                  tickDown  = core.getTickDown();
        GuiMouseDragEvent<T> dragEvent = core.getDragEvent();

        if (tickDown >= 0) {
            pushMouseDragEvent(dragEvent, mouseX, mouseY, dir, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);

            if (!dragEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }

            return !dragEvent.isCancelled();
        }
        return false;
    }

    default boolean dispatchMouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
        MGuiElementCore<T>     core        = getCore();
        GuiMouseScrollEvent<T> scrollEvent = core.getScrollEvent();

        GuiEventFactory.pushMouseScrollEvent(scrollEvent, mouseX, mouseY, ScrollDirection.getScrollDirection(scroll), scroll);
        onAnyEventFire(scrollEvent);

        if (!scrollEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.SCROLL);
            onMouseScrolled(scrollEvent);
        }

        return scrollEvent.isConsumed();
    }

    default boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        MGuiElementCore<T> core = getCore();
        if (!core.getStateManager().has(MGuiElementState.FOCUSED)) return false;

        GuiKeyEvent<T> keyEvent = core.getKeyEvent();

        pushKeyEvent(keyEvent, mouseX, mouseY, typedChar, keyCode);
        onAnyEventFire(keyEvent);

        if (!keyEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.KEY_TYPED);
            onKeyTyped(keyEvent);
        }

        return keyEvent.isConsumed();
    }

    default void dispatchPlaySound(Minecraft mc, SoundHandler soundHandler, MSoundSourceType source) {

        SoundEvent sound = getSoundPack().getSound(source);
        if (sound != null) {
            GuiSoundEvent<T>     soundEvent = getCore().getSoundEvent();
            GuiMouseMoveEvent<T> moveEvent  = getCore().getMoveEvent();
            pushSoundEvent(soundEvent, moveEvent.getMouseX(), moveEvent.getMouseY(), soundHandler, sound, source);
            onAnyEventFire(soundEvent);
            if (!soundEvent.isCancelled()) onPlaySound(soundEvent);
        }
    }

    //Рендеринг
    default void dispatchDraw(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (isVisible()) {
            dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
            dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
            dispatchDrawText(mc, mouseX, mouseY, partialTicks);
            dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
        }
    }

    default void dispatchDrawBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawBGEvent = getCore().getDrawBGEvent();
        GuiEventFactory.pushTickEvent(drawBGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (!drawBGEvent.isCancelled()) onDrawBackground(drawBGEvent);
    }

    default void dispatchDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawFGEvent = getCore().getDrawFGEvent();
        GuiEventFactory.pushTickEvent(drawFGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) onDrawForeground(drawFGEvent);
    }

    default void dispatchDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawTextEvent = getCore().getDrawTextEvent();
        GuiEventFactory.pushTickEvent(drawTextEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (!drawTextEvent.isCancelled()) onDrawText(drawTextEvent);
    }

    default void dispatchDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawLastEvent = getCore().getDrawLastEvent();
        GuiEventFactory.pushTickEvent(drawLastEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (!drawLastEvent.isCancelled()) onDrawLast(drawLastEvent);
    }

    //Интеграция с vanilla
    default int getHoverState(boolean mouseOver) { return !isEnabled() ? 0 : mouseOver ? 2 : 1; }
    default boolean mouseHover(Minecraft mc, int mouseX, int mouseY)   { return getCalculatedShape().contains(mouseX, mouseY); }
    default boolean mousePressed(Minecraft mc, int mouseX, int mouseY) { return isEnabled() && isVisible() && getCalculatedShape().contains(mouseX, mouseY); }
    default void mouseReleased(int mouseX, int mouseY)                 { dispatchMouseReleased(Minecraft.getMinecraft(), mouseX, mouseY, 0); }
    default void playPressSound(SoundHandler soundHandler)             { dispatchPlaySound(Minecraft.getMinecraft(), Minecraft.getMinecraft().getSoundHandler(), MSoundSourceType.PRESS); }
    default boolean isMouseOver()                                      { return getStateManager().has(MGuiElementState.HOVERED); }

    default void performClickFromVanilla() {
        if (!isEnabled() || !isVisible()) return;

        MutableGuiShape calcShape = getCalculatedShape();
        int             centerX   = (int) (calcShape.x() + calcShape.width() / 2f);
        int             centerY   = (int) (calcShape.y() + calcShape.height() / 2f);

        Minecraft mc = Minecraft.getMinecraft();
        dispatchMousePressed(mc, centerX, centerY, 0);
        dispatchMouseReleased(mc, centerX, centerY, 0);
    }

    //true if event is default logic
    default boolean onForgeClickIntegrationPre() { return false; }
    default void onForgeClickIntegrationPost() { }

    //Обработчики событий
    default void onDrawBackground(GuiTickEvent<T> event) {
        MGuiElementCore<T> core            = getCore();
        List<MGuiTexture>  textures        = core.getTexturePack().getCalculatedTextures(core.getStateManager());
        MutableGuiShape    calculatedShape = core.getCalculatedShape();
        for (MGuiTexture texture : textures) {
            texture.draw(
                    event.getMc(),
                    calculatedShape.x(), calculatedShape.y(),
                    calculatedShape.width(), calculatedShape.height()
            );
        }
    }

    default void onDrawForeground(GuiTickEvent<T> event) { }

    default void onDrawText(GuiTickEvent<T> event) {
        MGuiElementCore<T> core = getCore();

        String text = core.getGuiString().get();
        if (text != null && !text.isEmpty()) {
            int          color = core.getColorPack().getCalculatedColor(core.getStateManager(), getPackedFGColour());
            FontRenderer fr    = getFontRenderer();
            FontSize     fs    = getFontSize();

            float scale    = fs.getScale() * getTextScaleMultiplayer();
            float invScale = 1.0F / scale;

            MutableGuiShape calculatedInnerShape = core.getCalculatedInnerShape();

            float innerX = calculatedInnerShape.x();
            float innerY = calculatedInnerShape.y();
            float innerW = calculatedInnerShape.width();
            float innerH = calculatedInnerShape.height();

            GlStateManager.pushMatrix();
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GlStateManager.scale(scale, scale, 1.0F);

            MutableGuiVector calculatedTextOffsetTemp = core.getCalculatedTextOffsetTemp();
            MGuiRenderHelper.drawCenteredString(
                    fr, text,
                    (innerX + innerW / 2f) * invScale + calculatedTextOffsetTemp.x() * invScale,
                    (innerY + innerH / 2f) * invScale - (fr.FONT_HEIGHT / 2f) + calculatedTextOffsetTemp.y() * invScale,
                    color,
                    fs != FontSize.SMALL
            );

            GlStateManager.popMatrix();
        }
    }

    default void onDrawLast(GuiTickEvent<T> event)             { }

    default void onUpdate(GuiTickEvent<T> event)               { }
    default void onAnyEventFire(GuiEvent<T> event)             { }
    default void onMouseEnter(GuiMouseMoveEvent<T> event)      { }
    default void onMouseLeave(GuiMouseMoveEvent<T> event)      { }
    default void onMousePressed(GuiMouseClickEvent<T> event)   { }
    default void onMouseReleased(GuiMouseClickEvent<T> event)  { }
    default void onMouseDragged(GuiMouseDragEvent<T> event)    { }
    default void onMouseScrolled(GuiMouseScrollEvent<T> event) { }
    default void onKeyTyped(GuiKeyEvent<T> event)              { }
    default void onClick(GuiMouseClickEvent<T> event)          { }

    default void onPlaySound(GuiSoundEvent<T> event) {
        event.getHandler().playSound(PositionedSoundRecord.getMasterRecord(event.getSound(), 1.0F));
    }
}