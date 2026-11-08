/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.control.MGuiScrollbar;
import ru.mousecray.mouseproject.api.client.gui.dim.*;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiOrientation;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiPadding;
import ru.mousecray.mouseproject.api.client.gui.event.*;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;
import ru.mousecray.mouseproject.api.client.gui.misc.MoveDirection;
import ru.mousecray.mouseproject.api.client.gui.misc.ScrollDirection;
import ru.mousecray.mouseproject.core.MouseProject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper.calculateFlowComponentX;
import static ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper.calculateFlowComponentY;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MGuiScrollPanel<T extends MGuiScrollPanel<T>> implements MGuiElement<T> {
    protected final MGuiElementCore<T> core;

    @Nullable private MGuiPanel<?>   content;
    private           MGuiScrollbar  scrollbar;
    private           GuiOrientation orientation = GuiOrientation.VERTICAL;

    private final MGuiRenderHelper.ScissorState scissorState = new MGuiRenderHelper.ScissorState();

    private float   scrollValue        = 0;
    private float   contentSize        = 0;
    private boolean scrollEnabled      = true;
    private float   scrollbarThickness = 8f;

    private int id;

    public MGuiScrollPanel(GuiShape shape) {
        core = new MGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());

        GuiShape sbShape = new GuiShape(0, 0, scrollbarThickness, 100);
        scrollbar = new MGuiScrollbar(sbShape);

        scrollbar.setOnScroll(val -> {
            float diff = val - scrollValue;
            scrollValue = val;
            if (content != null) {
                if (orientation == GuiOrientation.VERTICAL) content.offsetCalculatedShape(0, -diff);
                else content.offsetCalculatedShape(-diff, 0);
            }
        });
    }

    public GuiOrientation getOrientation() { return orientation; }

    public void setOrientation(GuiOrientation orientation) {
        this.orientation = orientation;
        if (scrollbar != null) {
            scrollbar.setOrientation(orientation);
            if (orientation == GuiOrientation.VERTICAL) scrollbar.getShape().withWidth(scrollbarThickness);
            else scrollbar.getShape().withHeight(scrollbarThickness);
        }
    }

    public void setScrollbarThickness(float thickness) {
        scrollbarThickness = thickness;
        if (scrollbar != null) {
            if (orientation == GuiOrientation.VERTICAL) scrollbar.getShape().withWidth(thickness);
            else scrollbar.getShape().withHeight(thickness);
        }
    }

    public MGuiScrollbar getScrollbar()                 { return scrollbar; }
    public void setScrollEnabled(boolean scrollEnabled) { this.scrollEnabled = scrollEnabled; }

    public void setContent(@Nullable MGuiPanel<?> content) {
        this.content = content;
        if (content != null) {
            MGuiPanel<?> parent = getParent();
            MGuiScreen   screen = getScreen();
            if (parent != null) content.setParent(parent);
            if (screen != null) {
                content.setScreen(screen);
                content.setId(screen.genNextElementID());
            }
        }
    }

    @Nullable public MGuiPanel<?> getContent()               { return content; }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MGuiElementCore<T> getCore()            { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setScreen(@Nullable MGuiScreen screen) {
        MGuiElement.super.setScreen(screen);
        if (content != null) content.setScreen(screen);
        if (scrollbar != null) scrollbar.setScreen(screen);
    }

    @Override
    public void setParent(@Nullable MGuiPanel<?> parent) {
        MGuiElement.super.setParent(parent);
        if (parent != null && content != null) content.setParent(parent);
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
        MouseProject.LOGGER.warn("MGuiScrollPanel cannot support custom FontRenderer");
    }

    @Override public FontSize getFontSize() { return FontSize.NORMAL; }

    @Override
    public void setFontSize(FontSize size) {
        MouseProject.LOGGER.warn("MGuiScrollPanel cannot support custom FontSize");
    }

    @Override public float getTextScaleMultiplayer() { return 0.0f; }

    @Override
    public void setTextScaleMultiplayer(float multiplayer) {
        MouseProject.LOGGER.warn("MGuiScrollPanel cannot support custom TextScaleMultiplayer");
    }

    @Override public MutableGuiShape getCalculatedInnerShape() { return getCalculatedShape(); }
    @Override public GuiPadding getPadding()                   { return GuiPadding.ZERO(); }

    @Override
    public void setPadding(GuiPadding padding) {
        MouseProject.LOGGER.warn("MGuiScrollPanel cannot support padding");
    }

    @Override public MutableGuiVector getTextOffset() { return new MutableGuiVector(); }

    @Override
    public void calculate(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape available) {
        MutableGuiShape calculatedShape = getCalculatedShape();
        MutableGuiShape shape           = getShape();
        MGuiRenderHelper.calculateFlowComponentShape(
                calculatedShape, pDefSize, pContentSize,
                shape, getScaleRules(), available
        );

        if (content != null) {
            MutableGuiShape contentAvail = calculatedShape.copy().toMutable();
            boolean         isVert       = orientation == GuiOrientation.VERTICAL;

            float scaledSbThickness = isVert
                    ? calculateFlowComponentX(pDefSize, pContentSize, scrollbarThickness)
                    : calculateFlowComponentY(pDefSize, pContentSize, scrollbarThickness);

            if (isVert) {
                contentAvail.withHeight(99999f);
                if (scrollEnabled && scrollbar != null) contentAvail.withWidth(contentAvail.width() - scaledSbThickness);
            } else {
                contentAvail.withWidth(99999f);
                if (scrollEnabled && scrollbar != null) contentAvail.withHeight(contentAvail.height() - scaledSbThickness);
            }

            content.calculate(pDefSize, pContentSize, contentAvail);
            contentSize = isVert ? calculateTrueContentHeight() : calculateTrueContentWidth();

            float viewportSize = isVert ? calculatedShape.height() : calculatedShape.width();
            float maxScroll    = Math.max(0, contentSize - viewportSize);

            if (scrollValue > maxScroll) scrollValue = maxScroll;
            if (scrollValue < 0) scrollValue = 0;

            if (isVert) content.offsetCalculatedShape(0, -scrollValue);
            else content.offsetCalculatedShape(-scrollValue, 0);

            if (scrollEnabled && scrollbar != null) {
                scrollbar.updateSizes(viewportSize, contentSize);
                scrollbar.setScrollValue(scrollValue, false);

                scrollbar.getShape().withX(0).withY(0);
                if (isVert) scrollbar.getShape().withWidth(scrollbarThickness).withHeight(shape.height());
                else scrollbar.getShape().withWidth(shape.width()).withHeight(scrollbarThickness);

                scrollbar.calculate(pDefSize, pContentSize, calculatedShape);

                if (isVert) {
                    float dx = calculatedShape.x() + calculatedShape.width() - scrollbar.getCalculatedShape().width()
                            - scrollbar.getCalculatedShape().x();
                    scrollbar.offsetCalculatedShape(dx, 0);
                } else {
                    float dy = calculatedShape.y() + calculatedShape.height() - scrollbar.getCalculatedShape().height()
                            - scrollbar.getCalculatedShape().y();
                    scrollbar.offsetCalculatedShape(0, dy);
                }
            }
        }
    }

    @Override
    public void offsetCalculatedShape(float dx, float dy) {
        MGuiElement.super.offsetCalculatedShape(dx, dy);
        if (content != null) content.offsetCalculatedShape(dx, dy);
        if (scrollbar != null) scrollbar.offsetCalculatedShape(dx, dy);
    }

    public void applyScroll(float amount) {
        if (content == null) return;

        float oldScroll = scrollValue;
        scrollValue += amount;

        MutableGuiShape calculatedShape = getCalculatedShape();
        boolean         isVert          = orientation == GuiOrientation.VERTICAL;
        float           viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();
        float           maxScroll       = Math.max(0, contentSize - viewportSize);

        if (scrollValue < 0) scrollValue = 0;
        if (scrollValue > maxScroll) scrollValue = maxScroll;

        float diff = scrollValue - oldScroll;
        if (diff != 0) {
            if (isVert) content.offsetCalculatedShape(0, -diff);
            else content.offsetCalculatedShape(-diff, 0);

            if (scrollbar != null) scrollbar.setScrollValue(scrollValue, false);
        }
    }

    private float calculateTrueContentHeight() {
        if (content == null) return 0;
        return Math.max(0, findMaxBottom(content) - content.getCalculatedShape().y());
    }

    private float calculateTrueContentWidth() {
        if (content == null) return 0;
        return Math.max(0, findMaxRight(content) - content.getCalculatedShape().x());
    }

    private float findMaxBottom(MGuiElement<?> element) {
        if (!element.isVisible()) return element.getCalculatedShape().y();

        float max = element.getCalculatedShape().y() + element.getCalculatedShape().height();
        if (element instanceof MGuiPanel) {
            for (MGuiElement<?> child : ((MGuiPanel<?>) element).getChildren()) {
                max = Math.max(max, findMaxBottom(child));
            }
        }
        return max;
    }

    private float findMaxRight(MGuiElement<?> element) {
        if (!element.isVisible()) return element.getCalculatedShape().x();

        float max = element.getCalculatedShape().x() + element.getCalculatedShape().width();
        if (element instanceof MGuiPanel) {
            for (MGuiElement<?> child : ((MGuiPanel<?>) element).getChildren()) {
                max = Math.max(max, findMaxRight(child));
            }
        }
        return max;
    }

    //Диспетчеризация событий
    @Override
    public final void dispatchUpdate(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MGuiElement.super.dispatchUpdate(mc, mouseX, mouseY, partialTicks);

        MutableGuiShape calculatedShape = getCalculatedShape();

        boolean isVert       = orientation == GuiOrientation.VERTICAL;
        float   viewportSize = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
            scrollbar.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
        }
        if (content != null) content.dispatchUpdate(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public final void dispatchProcessHover(Minecraft mc, int mouseX, int mouseY) {
        MGuiElement.super.dispatchProcessHover(mc, mouseX, mouseY);
        if (!isVisible()) return;

        MutableGuiShape calculatedShape = getCalculatedShape();
        boolean         isVert          = orientation == GuiOrientation.VERTICAL;
        float           viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) scrollbar.dispatchProcessHover(mc, mouseX, mouseY);
        if (content != null) content.dispatchProcessHover(mc, mouseX, mouseY);

        boolean isHovered = calculatedShape.contains(mouseX, mouseY);
        if (isHovered && !getStateManager().has(MGuiElementState.HOVERED)) dispatchMouseEnter(mc, mouseX, mouseY);
        else if (!isHovered && getStateManager().has(MGuiElementState.HOVERED)) dispatchMouseLeave(mc, mouseX, mouseY);
    }

    @Override
    public final boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        MutableGuiShape calculatedShape = getCalculatedShape();
        if (!calculatedShape.contains(mouseX, mouseY)) return false;

        boolean isVert       = orientation == GuiOrientation.VERTICAL;
        float   viewportSize = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
            if (scrollbar.dispatchMousePressed(mc, mouseX, mouseY, mouseButton)) return true;
        }

        if (content != null && content.mouseHover(mc, mouseX, mouseY)) {
            if (content.dispatchMousePressed(mc, mouseX, mouseY, mouseButton)) return true;
        }

        if (!isEnabled() || !isVisible()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DISABLED);
            return false;
        }

        if (getStateManager().has(MGuiElementState.FAIL)) dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.FAIL);

        getCore().setTickDown(0);
        getStateManager().add(MGuiElementState.PRESSED);
        GuiMouseClickEvent<T> pressEvent = getCore().getPressEvent();
        GuiEventFactory.pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.PRESS);
            onMousePressed(pressEvent);
        }
        return true;
    }

    @Override
    public final void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        getCore().setTickDown(-1);
        getStateManager().remove(MGuiElementState.PRESSED);

        if (scrollEnabled && scrollbar != null) scrollbar.dispatchMouseReleased(mc, mouseX, mouseY, state);
        if (content != null) content.dispatchMouseReleased(mc, mouseX, mouseY, state);

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
    public final boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MoveDirection direction, int diffX, int diffY) {
        MGuiElementCore<T> core            = getCore();
        boolean            handled         = false;
        boolean            isVert          = orientation == GuiOrientation.VERTICAL;
        MutableGuiShape    calculatedShape = core.getCalculatedShape();
        float              viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

        if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
            if (scrollbar.dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY)) handled = true;
        }
        if (!handled && content != null) {
            handled = content.dispatchMouseDragged(mc, mouseX, mouseY, direction, diffX, diffY);
        }

        int                  tickDown  = core.getTickDown();
        GuiMouseDragEvent<T> dragEvent = core.getDragEvent();
        if (!handled && tickDown >= 0) {
            GuiEventFactory.pushMouseDragEvent(dragEvent, mouseX, mouseY, direction, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);
            if (!dragEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }
            return !dragEvent.isCancelled();
        }
        return handled;
    }

    @Override
    public final boolean dispatchMouseScrolled(Minecraft mc, int mouseX, int mouseY, int scroll) {
        MGuiElementCore<T> core = getCore();
        if (!core.getCalculatedShape().contains(mouseX, mouseY)) return false;

        if (content != null && content.dispatchMouseScrolled(mc, mouseX, mouseY, scroll)) return true;

        if (scrollEnabled) {
            float oldScroll = scrollValue;
            applyScroll(-scroll / 10f);
            GuiMouseScrollEvent<T> scrollEvent = core.getScrollEvent();
            GuiEventFactory.pushMouseScrollEvent(scrollEvent, mouseX, mouseY, ScrollDirection.getScrollDirection(scroll), scroll);
            onAnyEventFire(scrollEvent);
            if (!scrollEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.SCROLL);
                onMouseScrolled(scrollEvent);
            }
            return Float.compare(oldScroll, scrollValue) != 0 || scrollEvent.isConsumed();
        }
        return false;
    }

    @Override
    public final boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        if (!isVisible()) return false;

        if (content != null && content.dispatchKeyTyped(mc, mouseX, mouseY, typedChar, keyCode)) return true;

        MGuiElementCore<T> core = getCore();

        if (core.getStateManager().has(MGuiElementState.FOCUSED)) {
            GuiKeyEvent<T> keyEvent = core.getKeyEvent();
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
        MGuiElementCore<T> core        = getCore();
        GuiTickEvent<T>    drawBGEvent = core.getDrawBGEvent();
        GuiEventFactory.pushTickEvent(drawBGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawBGEvent);
        if (!drawBGEvent.isCancelled()) {
            onDrawBackground(drawBGEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean         isVert          = orientation == GuiOrientation.VERTICAL;
            MutableGuiShape calculatedShape = core.getCalculatedShape();
            float           viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawBackground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawForeground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MGuiElementCore<T> core        = getCore();
        GuiTickEvent<T>    drawFGEvent = core.getDrawFGEvent();
        GuiEventFactory.pushTickEvent(drawFGEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawFGEvent);
        if (!drawFGEvent.isCancelled()) {
            onDrawForeground(drawFGEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean         isVert          = orientation == GuiOrientation.VERTICAL;
            MutableGuiShape calculatedShape = core.getCalculatedShape();
            float           viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawForeground(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawText(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MGuiElementCore<T> core          = getCore();
        GuiTickEvent<T>    drawTextEvent = core.getDrawTextEvent();
        GuiEventFactory.pushTickEvent(drawTextEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawTextEvent);
        if (!drawTextEvent.isCancelled()) {
            onDrawText(drawTextEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawText(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean         isVert          = orientation == GuiOrientation.VERTICAL;
            MutableGuiShape calculatedShape = core.getCalculatedShape();
            float           viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawText(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    public final void dispatchDrawLast(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        MGuiElementCore<T> core          = getCore();
        GuiTickEvent<T>    drawLastEvent = core.getDrawLastEvent();
        GuiEventFactory.pushTickEvent(drawLastEvent, mouseX, mouseY, partialTicks);
        onAnyEventFire(drawLastEvent);
        if (!drawLastEvent.isCancelled()) {
            onDrawLast(drawLastEvent);

            if (content != null) {
                setupScissor(mc);
                content.dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
                restoreScissor();
            }

            boolean         isVert          = orientation == GuiOrientation.VERTICAL;
            MutableGuiShape calculatedShape = core.getCalculatedShape();
            float           viewportSize    = isVert ? calculatedShape.height() : calculatedShape.width();

            if (scrollEnabled && scrollbar != null && contentSize > viewportSize) {
                scrollbar.dispatchDrawLast(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    private void setupScissor(Minecraft mc) {
        int scale    = new ScaledResolution(mc).getScaleFactor();
        int scissorX = (int) (getCalculatedShape().x() * scale);
        int scissorY = (int) (mc.displayHeight - (getCalculatedShape().y() + getCalculatedShape().height()) * scale);
        int scissorW = (int) (getCalculatedShape().width() * scale);
        int scissorH = (int) (getCalculatedShape().height() * scale);

        MGuiRenderHelper.pushScissor(scissorX, scissorY, scissorW, scissorH, scissorState);
    }

    private void restoreScissor() { MGuiRenderHelper.popScissor(scissorState); }

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


}