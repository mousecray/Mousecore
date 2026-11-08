/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.component.color.MGuiColorPack;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.dim.*;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.AnchorPos;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiLayoutParams;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiMargin;
import ru.mousecray.mouseproject.api.client.gui.event.*;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;
import ru.mousecray.mouseproject.api.client.gui.misc.MoveDirection;
import ru.mousecray.mouseproject.api.client.gui.misc.ScrollDirection;
import ru.mousecray.mouseproject.core.MouseProject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;


@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MGuiPanel<T extends MGuiPanel<T>> implements MGuiElement<T> {
    protected final MGuiElementCore<T> core;

    protected final List<MGuiElement<?>> children = new ArrayList<>();

    protected final MutableGuiVector measureTemp        = new MutableGuiVector();
    protected final MutableGuiShape  childAvailableTemp = new MutableGuiShape();
    protected final MutableGuiShape  innerShapeTemp     = new MutableGuiShape();
    protected final float[]          marginTemp         = new float[4];

    private int id;

    @Nullable private MGuiElement<?> lastHoveredElement  = null;
    @Nullable private MGuiElement<?> lastSelectedElement = null;

    public MGuiPanel(GuiShape shape) {
        core = new MGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        getStateManager().setForbidden(MGuiElementState.FOCUSED, true);
        setColorPack(MGuiColorPack.CONTROL_SIMPLE());
    }

    public List<MGuiElement<?>> getChildren()                { return children; }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MGuiElementCore<T> getCore()            { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setScreen(@Nullable MGuiScreen screen) {
        MGuiElement.super.setScreen(screen);
        for (MGuiElement<?> child : children) child.setScreen(screen);
    }

    @Override public MGuiString getGuiString()               { return MGuiString.EMPTY(); }
    @Override public void setGuiString(MGuiString guiString) { }

    @Override
    public FontRenderer getFontRenderer() {
        if (getScreen() != null) return getScreen().getFontRenderer();
        return Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public void setFontRenderer(@Nullable FontRenderer fr) {
        MouseProject.LOGGER.warn("MGuiPanel cannot support custom FontRenderer");
    }

    @Override public FontSize getFontSize() { return FontSize.NORMAL; }

    @Override
    public void setFontSize(@Nullable FontSize size) {
        MouseProject.LOGGER.warn("MGuiPanel cannot support custom FontSize");
    }

    @Override public float getTextScaleMultiplayer() { return 0.0f; }

    @Override
    public void setTextScaleMultiplayer(float multiplayer) {
        MouseProject.LOGGER.warn("MGuiPanel cannot support custom TextScaleMultiplayer");
    }

    @Override public MutableGuiVector getTextOffset() { return new MutableGuiVector(); }

    public void addChild(MGuiElement<?> child, @Nullable GuiMargin margin, @Nullable AnchorPos anchor, @Nullable GuiVector offset) {
        children.add(child);

        child.getCore().setLayoutParams(new GuiLayoutParams(margin, anchor, offset));

        child.setParent(this);
        if (getScreen() != null) {
            child.setScreen(getScreen());
            child.setId(getScreen().genNextElementID());
        }
    }

    public void addChild(MGuiElement<?> child)               { addChild(child, GuiMargin.ZERO(), AnchorPos.TOP_LEFT, GuiVector.ZERO); }

    protected GuiMargin getChildMargin(MGuiElement<?> child) { return child.getCore().getLayoutParams().margin; }
    protected GuiVector getChildOffset(MGuiElement<?> child) { return child.getCore().getLayoutParams().offset; }

    @Nullable
    public MGuiElement<?> getLastSelectedElementRecursively() {
        if (lastSelectedElement instanceof MGuiPanel) {
            MGuiElement<?> nested = ((MGuiPanel<?>) lastSelectedElement).getLastSelectedElementRecursively();
            return nested != null ? nested : lastSelectedElement;
        }
        return lastSelectedElement;
    }

    protected float[] calculateScaledOffset(MGuiElement<?> child, IGuiVector parentDefaultSize, IGuiVector parentContentSize) {
        GuiVector offset = getChildOffset(child);
        return new float[]{
                MGuiRenderHelper.calculateFlowComponentX(parentDefaultSize, parentContentSize, offset.x()),
                MGuiRenderHelper.calculateFlowComponentY(parentDefaultSize, parentContentSize, offset.y())
        };
    }

    @Override
    public void onCalculated(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape innerCalcShape) {
        innerShapeTemp.withShape(innerCalcShape);
        layoutChildren(pDefSize, pContentSize, innerShapeTemp);
    }

    protected abstract void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MutableGuiShape inner);

    @Override
    public void offsetCalculatedShape(float dx, float dy) {
        MGuiElement.super.offsetCalculatedShape(dx, dy);
        for (MGuiElement<?> child : children) child.offsetCalculatedShape(dx, dy);
    }

    //Диспетчеризация событий
    @Override
    public final void dispatchUpdate(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MGuiElement.super.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
        for (MGuiElement<?> child : children) child.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public final void dispatchProcessHover(Minecraft mc, int mouseX, int mouseY) {
        MGuiElement.super.dispatchProcessHover(mc, mouseX, mouseY);

        if (!isVisible()) return;

        MGuiElement<?> currentHovered = null;

        for (int k = children.size() - 1; k >= 0; --k) {
            MGuiElement<?> child = children.get(k);
            if (child.mouseHover(mc, mouseX, mouseY) && child.isVisible()) {
                currentHovered = child;
                break;
            }
        }

        if (lastHoveredElement != currentHovered) {
            if (lastHoveredElement != null) lastHoveredElement.dispatchMouseLeave(mc, mouseX, mouseY);
            if (currentHovered != null) currentHovered.dispatchMouseEnter(mc, mouseX, mouseY);
            lastHoveredElement = currentHovered;
        }

        if (currentHovered != null) currentHovered.dispatchProcessHover(mc, mouseX, mouseY);
    }

    @Override
    public final void dispatchMouseLeave(Minecraft mc, int mouseX, int mouseY) {
        if (lastHoveredElement != null) {
            lastHoveredElement.dispatchMouseLeave(mc, mouseX, mouseY);
            lastHoveredElement = null;
        }

        MGuiElement.super.dispatchMouseLeave(mc, mouseX, mouseY);
    }

    @Override
    public final boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        if (!isEnabled() || !isVisible()) {
            if (getCalculatedShape().contains(mouseX, mouseY)) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DISABLED);
            }
            return false;
        }

        GuiMouseClickEvent<T> pressEvent = getCore().getPressEvent();
        GuiEventFactory.pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (pressEvent.isCancelled()) return true;

        for (int k = children.size() - 1; k >= 0; k--) {
            MGuiElement<?> child = children.get(k);
            if (child.dispatchMousePressed(mc, mouseX, mouseY, mouseButton)) {
                lastSelectedElement = child;
                return true;
            }
        }

        if (!getCalculatedShape().contains(mouseX, mouseY)) return false;

        if (getStateManager().has(MGuiElementState.FAIL)) dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.FAIL);

        getCore().setTickDown(0);
        getStateManager().add(MGuiElementState.PRESSED);

        dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.PRESS);
        onMousePressed(pressEvent);
        return true;
    }

    @Override
    public final void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        if (lastSelectedElement != null) {
            lastSelectedElement.dispatchMouseReleased(mc, mouseX, mouseY, state);
            lastSelectedElement = null;
            return;
        }

        getCore().setTickDown(-1);
        getStateManager().remove(MGuiElementState.PRESSED);

        GuiMouseClickEvent<T> releaseEvent = getCore().getReleaseEvent();
        GuiEventFactory.pushMouseClickEvent(releaseEvent, mouseX, mouseY);
        onAnyEventFire(releaseEvent);
        if (!releaseEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.RELEASE);
            onMouseReleased(releaseEvent);
            if (getCalculatedShape().contains(mouseX, mouseY)) {
                GuiMouseClickEvent<T> clickEvent = getCore().getClickEvent();
                GuiEventFactory.pushMouseClickEvent(clickEvent, mouseX, mouseY);
                onAnyEventFire(clickEvent);
                if (!clickEvent.isCancelled()) {
                    dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.CLICK);
                    onClick(clickEvent);
                }
            }
        }
    }

    @Override
    public final boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MoveDirection dir, int diffX, int diffY) {
        if (lastSelectedElement != null) {
            return lastSelectedElement.dispatchMouseDragged(mc, mouseX, mouseY, dir, diffX, diffY);
        }

        int tickDown = getCore().getTickDown();
        if (tickDown >= 0) {
            GuiMouseDragEvent<T> dragEvent = getCore().getDragEvent();
            GuiEventFactory.pushMouseDragEvent(dragEvent, mouseX, mouseY, dir, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);
            if (!dragEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }
            return !dragEvent.isCancelled();
        }
        return false;
    }

    @Override
    public final boolean dispatchMouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
        if (!getCalculatedShape().contains(mouseX, mouseY) || !isVisible()) return false;

        for (int k = children.size() - 1; k >= 0; k--) {
            MGuiElement<?> child = children.get(k);
            if (child.isVisible() && child.getCalculatedShape().contains(mouseX, mouseY)) {
                if (child.dispatchMouseScrolled(mc, mouseX, mouseY, scroll)) return true;
            }
        }

        GuiMouseScrollEvent<T> scrollEvent = getCore().getScrollEvent();
        GuiEventFactory.pushMouseScrollEvent(
                scrollEvent, mouseX, mouseY, ScrollDirection.getScrollDirection(scroll), scroll
        );
        onAnyEventFire(scrollEvent);
        if (!scrollEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.SCROLL);
            onMouseScrolled(scrollEvent);
        }

        return scrollEvent.isConsumed();
    }

    @Override
    public final boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        if (!isVisible()) return false;

        for (int k = children.size() - 1; k >= 0; k--) {
            MGuiElement<?> child = children.get(k);
            if (child.dispatchKeyTyped(mc, mouseX, mouseY, typedChar, keyCode)) return true;
        }

        if (getStateManager().has(MGuiElementState.FOCUSED)) {
            GuiKeyEvent<T> keyEvent = getCore().getKeyEvent();
            GuiEventFactory.pushKeyEvent(keyEvent, mouseX, mouseY, typedChar, keyCode);
            onAnyEventFire(keyEvent);

            if (!keyEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.KEY_TYPED);
                onKeyTyped(keyEvent);
            }

            return keyEvent.isConsumed();
        }
        return false;
    }

    //Рендеринг
    @Override
    public final void dispatchDrawBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawBGEvent = getCore().getDrawBGEvent();
        GuiEventFactory.pushTickEvent(drawBGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (!drawBGEvent.isCancelled()) {
            onDrawBackground(drawBGEvent);
            for (MGuiElement<?> child : children) {
                child.dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawFGEvent = getCore().getDrawFGEvent();
        GuiEventFactory.pushTickEvent(drawFGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) {
            onDrawForeground(drawFGEvent);
            for (MGuiElement<?> child : children) {
                child.dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawTextEvent = getCore().getDrawTextEvent();
        GuiEventFactory.pushTickEvent(drawTextEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (!drawTextEvent.isCancelled()) {
            onDrawText(drawTextEvent);
            for (MGuiElement<?> child : children) {
                child.dispatchDrawText(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiTickEvent<T> drawLastEvent = getCore().getDrawLastEvent();
        GuiEventFactory.pushTickEvent(drawLastEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (!drawLastEvent.isCancelled()) {
            onDrawLast(drawLastEvent);
            for (MGuiElement<?> child : children) {
                child.dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
            }
        }
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

    public void collectElements() {
        MGuiScreen screen = getScreen();
        if (screen == null) return;
        for (MGuiElement<?> child : children) {
            if (child instanceof MGuiPanel) ((MGuiPanel<?>) child).collectElements();
            else if (child instanceof GuiButton) screen.getButtonList().add((GuiButton) child);
            else if (child instanceof GuiLabel) screen.getLabelList().add((GuiLabel) child);
            else if (child instanceof GuiTextField) screen.getFieldsList().add((GuiTextField) child);
        }
    }

    public void removeAllChildren() {
        children.clear();
        onChildrenCleared();
    }

    protected void onChildrenCleared() { }
}