/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiButton;
import ru.mousecray.mouseproject.api.client.gui.MGuiPanel;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexture;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.dim.*;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiOrientation;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleType;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseClickEvent;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseDragEvent;
import ru.mousecray.mouseproject.api.client.gui.event.GuiTickEvent;
import ru.mousecray.mouseproject.core.MouseProject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MGuiScrollbar extends MGuiPanel<MGuiScrollbar> {
    private GuiOrientation orientation = GuiOrientation.VERTICAL;

    private final ScrollbarArrow minusButton;
    private final ScrollbarArrow plusButton;
    private final ScrollbarThumb thumb;

    private MGuiTexturePack thumbForegroundTexturePack = MGuiTexturePack.EMPTY();

    private IGuiVector lastParentDefaultSize, lastParentContentSize;

    private float contentSize  = 1f;
    private float viewportSize = 1f;
    private float scrollValue  = 0f;
    private float scrollStep   = 20f;

    private Consumer<Float> onScroll;

    public MGuiScrollbar(GuiShape shape) {
        super(shape);

        minusButton = new ScrollbarArrow(true);
        plusButton = new ScrollbarArrow(false);
        thumb = new ScrollbarThumb();

        addChild(minusButton);
        addChild(plusButton);
        addChild(thumb);

        updateOrientationState();
    }

    public GuiOrientation getOrientation() { return orientation; }

    public void setOrientation(GuiOrientation orientation) {
        if (getScreen() != null) {
            MouseProject.LOGGER.warn(
                    "Orientation cannot be setup immediately to MGuiScrollbar that added to container." +
                            " It set now, but actual element size will be updated on the next gui size calculation."
            );
        }
        if (this.orientation != orientation) {
            this.orientation = orientation;
            updateOrientationState();
        }
    }

    private void updateOrientationState() {
        boolean isVert = orientation == GuiOrientation.VERTICAL;

        minusButton.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT));
        plusButton.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT));
        thumb.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT));

        IGuiVector minusPos  = isVert ? GuiVector.of(203, 0) : GuiVector.of(203, 22);
        IGuiVector minusSize = isVert ? GuiVector.of(11, 7) : GuiVector.of(7, 11);

        IGuiVector plusPos  = isVert ? GuiVector.of(214, 0) : GuiVector.of(214, 22);
        IGuiVector plusSize = isVert ? GuiVector.of(11, 7) : GuiVector.of(7, 11);

        IGuiVector thumbPos  = isVert ? GuiVector.of(192, 0) : GuiVector.of(192, 31);
        IGuiVector thumbSize = isVert ? GuiVector.of(11, 10) : GuiVector.of(10, 11);

        IGuiVector fgPos  = isVert ? GuiVector.of(224, 0) : GuiVector.of(224, 7);
        IGuiVector fgSize = isVert ? GuiVector.of(5, 2) : GuiVector.of(2, 5);

        setMinusArrowTexturePack(MGuiTexturePack.Builder
                .create(CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE, minusPos, minusSize)
                .addTexture(0)
                .addTexture(1, MGuiElementState.HOVERED)
                .addTexture(2, MGuiElementState.PRESSED)
                .build()
        );

        setPlusArrowTexturePack(MGuiTexturePack.Builder
                .create(CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE, plusPos, plusSize)
                .addTexture(0)
                .addTexture(1, MGuiElementState.HOVERED)
                .addTexture(2, MGuiElementState.PRESSED)
                .build()
        );

        setThumbTexturePack(MGuiTexturePack.Builder
                .create(CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE, thumbPos, thumbSize)
                .addTexture(0)
                .addTexture(1, MGuiElementState.HOVERED)
                .addTexture(2, MGuiElementState.PRESSED)
                .build()
        );

        setThumbForegroundTexturePack(MGuiTexturePack.Builder
                .create(CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE, fgPos, fgSize)
                .addTexture(0)
                .addTexture(1, MGuiElementState.HOVERED)
                .addTexture(2, MGuiElementState.PRESSED)
                .build()
        );
    }

    public MGuiTexturePack getMinusArrowTexturePack() { return minusButton.getTexturePack(); }

    public void setMinusArrowTexturePack(MGuiTexturePack pack) {
        Objects.requireNonNull(pack, "texturePack cannot be null. Use MGuiTexturePack.EMPTY() instead.");
        minusButton.setTexturePack(pack);
    }

    public MGuiTexturePack getPlusArrowTexturePack() { return plusButton.getTexturePack(); }

    public void setPlusArrowTexturePack(MGuiTexturePack pack) {
        Objects.requireNonNull(pack, "texturePack cannot be null. Use MGuiTexturePack.EMPTY() instead.");
        plusButton.setTexturePack(pack);
    }

    public MGuiTexturePack getThumbTexturePack() { return thumb.getTexturePack(); }

    public void setThumbTexturePack(MGuiTexturePack pack) {
        Objects.requireNonNull(pack, "texturePack cannot be null. Use MGuiTexturePack.EMPTY() instead.");
        thumb.setTexturePack(pack);
    }

    public MGuiTexturePack getThumbForegroundTexturePack() { return thumbForegroundTexturePack; }

    public void setThumbForegroundTexturePack(MGuiTexturePack pack) {
        Objects.requireNonNull(pack, "texturePack cannot be null. Use MGuiTexturePack.EMPTY() instead.");
        thumbForegroundTexturePack = pack;
    }

    @Override
    public void onClick(GuiMouseClickEvent<MGuiScrollbar> event) {
        boolean isVert   = orientation == GuiOrientation.VERTICAL;
        float   clickPos = isVert ? (event.getMouseY() - getCalculatedShape().y()) : (event.getMouseX() - getCalculatedShape().x());
        float thumbPos = isVert ? (thumb.getCalculatedShape().y() - getCalculatedShape().y())
                : (thumb.getCalculatedShape().x() - getCalculatedShape().x());
        float thumbEnd = thumbPos + (isVert ? thumb.getCalculatedShape().height() : thumb.getCalculatedShape().width());

        if (clickPos < thumbPos) setScrollValue(scrollValue - viewportSize, true);
        else if (clickPos > thumbEnd) setScrollValue(scrollValue + viewportSize, true);
    }

    public void setOnScroll(Consumer<Float> onScroll) { this.onScroll = onScroll; }
    public void setScrollStep(float step)             { scrollStep = step; }

    public void updateSizes(float viewportSize, float contentSize) {
        this.viewportSize = viewportSize;
        this.contentSize = contentSize;
        recalculateThumb();
    }

    public void setScrollValue(float value, boolean notify) {
        float maxScroll = Math.max(0, contentSize - viewportSize);
        value = Math.max(0, Math.min(value, maxScroll));

        if (Float.compare(scrollValue, value) != 0) {
            scrollValue = value;
            recalculateThumb();
            if (notify && onScroll != null) onScroll.accept(scrollValue);
        }
    }

    public float getScrollValue() { return scrollValue; }

    private void recalculateThumb() {
        MutableGuiShape inner = getCalculatedShape();
        if (inner.width() <= 0 || inner.height() <= 0) return;

        boolean isVert      = orientation == GuiOrientation.VERTICAL;
        float   thickness   = isVert ? inner.width() : inner.height();
        float   totalLength = isVert ? inner.height() : inner.width();

        float trackSize = totalLength - (thickness * 2);
        float maxScroll = Math.max(0, contentSize - viewportSize);

        if (maxScroll <= 0 || trackSize <= 0) {
            thumb.getStateManager().add(MGuiElementState.DISABLED);
            thumb.getStateManager().add(MGuiElementState.HIDDEN);
        } else {
            thumb.getStateManager().remove(MGuiElementState.DISABLED);
            thumb.getStateManager().remove(MGuiElementState.HIDDEN);

            float thumbRatio  = viewportSize / contentSize;
            float thumbLength = Math.max(thickness, trackSize * thumbRatio);

            float scrollRatio = scrollValue / maxScroll;
            float thumbOffset = thickness + (scrollRatio * (trackSize - thumbLength));

            if (lastParentDefaultSize != null && lastParentContentSize != null) {
                if (isVert) childAvailableTemp.withX(inner.x()).withY(inner.y() + thumbOffset)
                        .withWidth(thickness).withHeight(thumbLength);
                else childAvailableTemp.withX(inner.x() + thumbOffset).withY(inner.y())
                        .withWidth(thumbLength).withHeight(thickness);
                thumb.calculate(lastParentDefaultSize, lastParentContentSize, childAvailableTemp);
            }
        }
    }

    @Override
    public void calculate(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape available) {
        lastParentDefaultSize = pDefSize;
        lastParentContentSize = pContentSize;
        super.calculate(pDefSize, pContentSize, available);
    }

    @Override
    protected void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MutableGuiShape inner) {
        boolean isVert      = orientation == GuiOrientation.VERTICAL;
        float   thickness   = isVert ? inner.width() : inner.height();
        float   totalLength = isVert ? inner.height() : inner.width();

        childAvailableTemp.withX(inner.x()).withY(inner.y()).withWidth(thickness).withHeight(thickness);
        minusButton.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);

        if (isVert) childAvailableTemp.withX(inner.x()).withY(inner.y() + totalLength - thickness);
        else childAvailableTemp.withX(inner.x() + totalLength - thickness).withY(inner.y());
        plusButton.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);

        recalculateThumb();
    }

    private class ScrollbarArrow extends MGuiButton<ScrollbarArrow> {
        private final boolean isMinus;

        public ScrollbarArrow(boolean isMinus) {
            super(new GuiShape(0, 0, 0, 0));
            this.isMinus = isMinus;
        }

        @Override
        public void onClick(GuiMouseClickEvent<ScrollbarArrow> event) {
            if (isMinus) setScrollValue(scrollValue - scrollStep, true);
            else setScrollValue(scrollValue + scrollStep, true);
        }
    }

    private class ScrollbarThumb extends MGuiButton<ScrollbarThumb> {
        public ScrollbarThumb() {
            super(new GuiShape(0, 0, 0, 0));
        }

        @Override
        public void onMouseDragged(GuiMouseDragEvent<ScrollbarThumb> e) {
            if (e.isCancelled()) return;

            float maxScroll = Math.max(0, contentSize - viewportSize);
            if (maxScroll <= 0) return;

            boolean isVert          = orientation == GuiOrientation.VERTICAL;
            float   scrollableTrack = getScrollableTrack(isVert);

            if (scrollableTrack <= 0.001f) return;

            float diff        = isVert ? e.getDiffY() : e.getDiffX();
            float moveRatio   = diff / scrollableTrack;
            float scrollDelta = moveRatio * maxScroll;

            setScrollValue(scrollValue + scrollDelta, true);
            e.consume();
        }

        private float getScrollableTrack(boolean isVert) {
            float thickness = isVert ? getCalculatedShape().width() : getCalculatedShape().height();
            float totalTrackLength = isVert
                    ? MGuiScrollbar.this.getCalculatedShape().height()
                    : MGuiScrollbar.this.getCalculatedShape().width();

            float trackSize   = totalTrackLength - (thickness * 2);
            float thumbLength = isVert ? getCalculatedShape().height() : getCalculatedShape().width();
            return trackSize - thumbLength;
        }

        @Override
        public void onClick(GuiMouseClickEvent<ScrollbarThumb> event) { }

        @Override
        public void onDrawForeground(GuiTickEvent<ScrollbarThumb> event) {
            super.onDrawForeground(event);

            MGuiTexture fgTex = thumbForegroundTexturePack.getCalculatedTexture(getStateManager());
            if (fgTex != null) {
                float w         = getCalculatedShape().width();
                float h         = getCalculatedShape().height();
                float linesSize = Math.min(w, h);
                float linesX    = getCalculatedShape().x() + (w - linesSize) / 2f;
                float linesY    = getCalculatedShape().y() + (h - linesSize) / 2f;

                fgTex.draw(event.getMc(), linesX, linesY, linesSize, linesSize);
            }
        }
    }
}