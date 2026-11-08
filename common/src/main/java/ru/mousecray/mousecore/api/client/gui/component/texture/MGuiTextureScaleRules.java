/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.texture;

import java.util.*;

public class MGuiTextureScaleRules {
    private final Set<MGuiTextureScaleType> scaleTypes  = new HashSet<>();
    private       float                     multiplierX = 1.0f;
    private       float                     multiplierY = 1.0f;
    private       float                     gapX        = 0f;
    private       float                     gapY        = 0f;

    public MGuiTextureScaleRules(MGuiTextureScaleType... types) {
        for (MGuiTextureScaleType type : types) addType(type);
    }

    private void addType(MGuiTextureScaleType newType) {
        List<MGuiTextureScaleType> toRemove = new ArrayList<>();
        for (MGuiTextureScaleType existing : scaleTypes) {
            if (isIncompatible(existing, newType)) toRemove.add(existing);
        }
        toRemove.forEach(scaleTypes::remove);

        scaleTypes.add(newType);

        if (scaleTypes.contains(MGuiTextureScaleType.STRETCH)) {
            scaleTypes.remove(MGuiTextureScaleType.STRETCH_HORIZONTAL);
            scaleTypes.remove(MGuiTextureScaleType.STRETCH_VERTICAL);
        }
        if (scaleTypes.contains(MGuiTextureScaleType.FILL)) {
            scaleTypes.remove(MGuiTextureScaleType.FILL_HORIZONTAL);
            scaleTypes.remove(MGuiTextureScaleType.FILL_VERTICAL);
        }
    }

    private boolean isIncompatible(MGuiTextureScaleType a, MGuiTextureScaleType b) {
        MGuiTextureScaleType.Category catA = a.getCategory();
        MGuiTextureScaleType.Category catB = b.getCategory();

        if (catA == catB && axesOverlap(a, b)) return true;
        return axesOverlap(a, b);
    }

    private boolean axesOverlap(MGuiTextureScaleType a, MGuiTextureScaleType b) {
        Set<MGuiTextureScaleType.Axes> axesA = a.getAxes();
        Set<MGuiTextureScaleType.Axes> axesB = b.getAxes();
        axesA.retainAll(axesB);
        return !axesA.isEmpty();
    }

    public MGuiTextureScaleRules setMultipliers(float multiplierX, float multiplierY) {
        this.multiplierX = multiplierX;
        this.multiplierY = multiplierY;
        return this;
    }

    public MGuiTextureScaleRules setGaps(float gapX, float gapY) {
        this.gapX = gapX;
        this.gapY = gapY;
        return this;
    }

    public MGuiTextureScaleRules setMultiplayerX(float multiplierX) {
        this.multiplierX = multiplierX;
        return this;
    }
    public MGuiTextureScaleRules setMultiplayerY(float multiplierY) {
        this.multiplierY = multiplierY;
        return this;
    }
    public MGuiTextureScaleRules setMultiplier(float multiplier) { return setMultipliers(multiplier, multiplier); }

    public float getMultiplierX()                                { return multiplierX; }
    public float getMultiplierY()                                { return multiplierY; }
    public float getGapX()                                       { return gapX; }
    public float getGapY()                                       { return gapY; }

    public ScaleMode getModeX() {
        if (scaleTypes.contains(MGuiTextureScaleType.FILL) || scaleTypes.contains(MGuiTextureScaleType.FILL_HORIZONTAL))
            return ScaleMode.FILL;
        if (scaleTypes.stream().anyMatch(
                t -> t.getCategory() == MGuiTextureScaleType.Category.SINGLE
                        && t.getAxes().contains(MGuiTextureScaleType.Axes.HORIZONTAL)
        )) return ScaleMode.SINGLE;
        return ScaleMode.STRETCH;
    }

    public ScaleMode getModeY() {
        if (scaleTypes.contains(MGuiTextureScaleType.FILL) || scaleTypes.contains(MGuiTextureScaleType.FILL_VERTICAL))
            return ScaleMode.FILL;
        if (scaleTypes.stream().anyMatch(
                t -> t.getCategory() == MGuiTextureScaleType.Category.SINGLE
                        && t.getAxes().contains(MGuiTextureScaleType.Axes.VERTICAL)
        )) return ScaleMode.SINGLE;
        return ScaleMode.STRETCH;
    }

    public TextureAnchor getAnchorX() {
        if (scaleTypes.contains(MGuiTextureScaleType.SINGLE_HORIZONTAL_CENTER)) return TextureAnchor.CENTER;
        if (scaleTypes.contains(MGuiTextureScaleType.SINGLE_HORIZONTAL_RIGHT)) return TextureAnchor.MAX;
        return TextureAnchor.MIN;
    }

    public TextureAnchor getAnchorY() {
        if (scaleTypes.contains(MGuiTextureScaleType.SINGLE_VERTICAL_CENTER)) return TextureAnchor.CENTER;
        if (scaleTypes.contains(MGuiTextureScaleType.SINGLE_VERTICAL_BOTTOM)) return TextureAnchor.MAX;
        return TextureAnchor.MIN;
    }

    public Set<MGuiTextureScaleType> getScaleTypes() { return Collections.unmodifiableSet(scaleTypes); }

    public enum ScaleMode {STRETCH, FILL, SINGLE}

    public enum TextureAnchor {MIN, CENTER, MAX}
}