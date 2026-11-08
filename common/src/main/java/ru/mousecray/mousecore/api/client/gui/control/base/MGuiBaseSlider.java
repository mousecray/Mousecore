/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control.base;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiButton;
import ru.mousecray.mouseproject.api.client.gui.MGuiPanel;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MGuiSoundPack;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.dim.*;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiOrientation;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleType;
import ru.mousecray.mouseproject.api.client.gui.event.GuiEventFactory;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseClickEvent;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseDragEvent;
import ru.mousecray.mouseproject.api.client.gui.event.GuiSliderChangedEvent;
import ru.mousecray.mouseproject.core.MouseProject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MGuiBaseSlider<T extends MGuiBaseSlider<T>> extends MGuiPanel<T> {
    private final MGuiButton<?> knob;
    private final MGuiButton<?> track;

    private       int value;
    private final int min, max, range;

    private GuiOrientation orientation;
    private float          progress = 0f;

    private final float originalKnobWidth;
    private final float originalKnobHeight;

    private final GuiSliderChangedEvent<T> sliderChangedEvent = new GuiSliderChangedEvent<>();

    private Consumer<GuiSliderChangedEvent<T>> onSliderChangedListener = null;

    private IGuiVector lastParentDefaultSize, lastParentContentSize;

    public MGuiBaseSlider(GuiShape iShape, float knobWidth, float knobHeight, int min, int max, GuiOrientation orientation) {
        super(iShape);
        this.orientation = orientation;
        this.min = min;
        this.max = Math.max(max, min);
        range = this.max - min;

        originalKnobWidth = knobWidth;
        originalKnobHeight = knobHeight;

        Minecraft mc = Minecraft.getMinecraft();
        sliderChangedEvent.bind(mc, self());

        class TrackButton extends MGuiButton<TrackButton> {
            public TrackButton() {
                super(iShape);
                setSoundPack(MGuiSoundPack.Builder
                        .create()
                        .addSound(MSoundSourceType.PRESS, SoundEvents.UI_BUTTON_CLICK)
                        .build()
                );
            }

            @Override public void onClick(GuiMouseClickEvent<TrackButton> e) { updateFromMouse(e.getMouseX(), e.getMouseY()); }
        }

        track = new TrackButton();
        track.setScaleRules(new GuiScaleRules(GuiScaleType.PARENT));
        addChild(track);

        class KnobButton extends MGuiButton<KnobButton> {
            public KnobButton() {
                super(new GuiShape(0, 0, knobWidth, knobHeight));
                setSoundPack(MGuiSoundPack.Builder
                        .create()
                        .addSound(MSoundSourceType.PRESS, SoundEvents.UI_BUTTON_CLICK)
                        .build()
                );
            }

            @Override
            public void onMouseDragged(GuiMouseDragEvent<KnobButton> e) {
                if (e.isCancelled()) return;
                updateFromMouse(e.getMouseX(), e.getMouseY());
            }

            @Override
            public void onClick(GuiMouseClickEvent<KnobButton> e) {
                if (e.isCancelled()) return;
                updateFromMouse(e.getMouseX(), e.getMouseY());
            }
        }

        knob = new KnobButton();
        addChild(knob);

        updateOrientationState();
        setValueInternal(min, false, 0, 0);
    }

    public void setTrackTexturePack(MGuiTexturePack pack) { track.setTexturePack(pack); }
    public void setKnobTexturePack(MGuiTexturePack pack)  { knob.setTexturePack(pack); }
    public MGuiTexturePack getTrackTexturePack()          { return track.getTexturePack(); }
    public MGuiTexturePack getKnobTexturePack()           { return knob.getTexturePack(); }

    public GuiOrientation getOrientation()                { return orientation; }

    public void setOrientation(GuiOrientation orientation) {
        if (getScreen() != null) {
            MouseProject.LOGGER.warn(
                    "Orientation cannot be setup immediately to MGuiBaseSlider that added to container." +
                            " It set now, but actual element size will be updated on the next gui size calculation."
            );
        }
        if (this.orientation != orientation) {
            this.orientation = orientation;
            updateOrientationState();
            onOrientationChanged();
        }
    }

    private void updateOrientationState() {
        boolean isVert = orientation == GuiOrientation.VERTICAL;
        float   kw     = isVert ? originalKnobHeight : originalKnobWidth;
        float   kh     = isVert ? originalKnobWidth : originalKnobHeight;

        knob.getShape().withWidth(kw).withHeight(kh);
        knob.setScaleRules(new GuiScaleRules(isVert ? GuiScaleType.ORIGIN_HORIZONTAL : GuiScaleType.ORIGIN_VERTICAL));
        recalculateKnobPosition();
    }

    protected void onOrientationChanged() { }

    private void updateFromMouse(int mouseX, int mouseY) {
        MutableGuiShape inner  = getCalculatedShape();
        boolean         isVert = orientation == GuiOrientation.VERTICAL;

        float knobW = knob.getCalculatedShape().width();
        float knobH = knob.getCalculatedShape().height();

        float trackLength = isVert ? inner.height() - knobH : inner.width() - knobW;
        if (trackLength <= 0) return;

        float rel, newProgress;
        if (isVert) {
            rel = (mouseY - inner.y()) - knobH / 2f;
            newProgress = 1.0f - MathHelper.clamp(rel / trackLength, 0f, 1f);
        } else {
            rel = (mouseX - inner.x()) - knobW / 2f;
            newProgress = MathHelper.clamp(rel / trackLength, 0f, 1f);
        }

        if (Float.compare(progress, newProgress) == 0) return;

        int newValue = min + Math.round(newProgress * range);
        if (value != newValue) setValueInternal(newValue, true, mouseX, mouseY);
    }

    public void setOnSliderChangedListener(@Nullable Consumer<GuiSliderChangedEvent<T>> listener) {
        onSliderChangedListener = listener;
    }

    public Consumer<GuiSliderChangedEvent<T>> getOnSliderChangedListener() { return onSliderChangedListener; }
    protected void onSliderChanged(GuiSliderChangedEvent<T> event)         { }

    public int getValue()                                                  { return value; }
    public void setValue(int newValue)                                     { setValueInternal(newValue, true, 0, 0); }
    public void setValue(int newValue, boolean notify)                     { setValueInternal(newValue, notify, 0, 0); }

    private void setValueInternal(int newValue, boolean notify, int mouseX, int mouseY) {
        newValue = MathHelper.clamp(newValue, min, max);
        if (value == newValue) return;

        int oldValue = value;
        value = newValue;
        progress = range == 0 ? 0f : (float) (newValue - min) / range;

        if (notify) {
            GuiEventFactory.pushSliderChangedEvent(sliderChangedEvent, mouseX, mouseY, oldValue, value);
            onAnyEventFire(sliderChangedEvent);
            if (!sliderChangedEvent.isCancelled()) {
                onSliderChanged(sliderChangedEvent);
                if (onSliderChangedListener != null) onSliderChangedListener.accept(sliderChangedEvent);
            } else {
                value = oldValue;
                progress = range == 0 ? 0f : (float) (value - min) / range;
            }
        }
        recalculateKnobPosition();
    }

    public float getProgress() { return progress; }

    @Override
    protected void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MutableGuiShape inner) {
        childAvailableTemp.withShape(inner);
        track.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);

        recalculateKnobPosition();
    }

    private void recalculateKnobPosition() {
        MutableGuiShape inner = getCalculatedShape();
        if (inner.width() <= 0 || inner.height() <= 0) return;

        float knobW = knob.getShape().width();
        float knobH = knob.getShape().height();

        if (lastParentDefaultSize != null && lastParentContentSize != null) {
            knob.measurePreferred(
                    lastParentDefaultSize, lastParentContentSize,
                    inner.width(), inner.height(), measureTemp
            );
            knobW = measureTemp.x();
            knobH = measureTemp.y();
        }

        boolean isVert      = orientation == GuiOrientation.VERTICAL;
        float   trackLength = isVert ? inner.height() - knobH : inner.width() - knobW;

        if (trackLength <= 0) {
            progress = 0f;
            value = min;
            trackLength = 0;
        }

        float knobPrimary;
        if (isVert) knobPrimary = (1f - progress) * trackLength;
        else knobPrimary = progress * trackLength;

        float knobSecondary = isVert ? (inner.width() - knobW) / 2f : (inner.height() - knobH) / 2f;

        float knobX = inner.x() + (isVert ? knobSecondary : knobPrimary);
        float knobY = inner.y() + (isVert ? knobPrimary : knobSecondary);

        childAvailableTemp.withX(knobX).withY(knobY).withWidth(knobW).withHeight(knobH);

        if (lastParentDefaultSize != null && lastParentContentSize != null) {
            knob.calculate(lastParentDefaultSize, lastParentContentSize, childAvailableTemp);
        } else {
            knob.calculate(
                    GuiVector.of(inner.width(), inner.height()),
                    GuiVector.of(inner.width(), inner.height()),
                    childAvailableTemp
            );
        }
    }

    @Override
    public void calculate(IGuiVector pDefSize, IGuiVector pContentSize, IGuiShape available) {
        lastParentDefaultSize = pDefSize;
        lastParentContentSize = pContentSize;
        super.calculate(pDefSize, pContentSize, available);
    }
}