/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTextureScaleRules;
import ru.mousecray.mouseproject.api.client.gui.control.base.MGuiBaseSlider;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiOrientation;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.HOVERED;
import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.PRESSED;
import static ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTextureScaleType.*;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class MGuiSimpleSlider extends MGuiBaseSlider<MGuiSimpleSlider> {

    public MGuiSimpleSlider(GuiShape shape, float knobWidth, float knobHeight, int min, int max, GuiOrientation orientation) {
        super(shape, knobWidth, knobHeight, min, max, orientation);
        updateTextures();
    }

    @Override
    protected void onOrientationChanged() {
        super.onOrientationChanged();
        updateTextures();
    }

    private void updateTextures() {
        boolean isVert = getOrientation() == GuiOrientation.VERTICAL;

        MGuiTextureScaleRules trackScaleRules = isVert
                ? new MGuiTextureScaleRules(FILL_VERTICAL, SINGLE_HORIZONTAL_LEFT).setMultipliers(0.7f, 0.5f)
                : new MGuiTextureScaleRules(FILL_HORIZONTAL, SINGLE_VERTICAL_TOP).setMultipliers(0.5f, 0.7f);

        IGuiVector trackPos  = isVert ? GuiVector.of(230, 8) : GuiVector.of(230, 0);
        IGuiVector trackSize = isVert ? GuiVector.of(7, 18) : GuiVector.of(18, 7);

        IGuiVector knobPos  = isVert ? GuiVector.of(90, 22) : GuiVector.of(90, 0);
        IGuiVector knobSize = isVert ? GuiVector.of(5) : GuiVector.of(5, 7);

        setTrackTexturePack(MGuiTexturePack.Builder
                .create(CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE, trackPos, trackSize)
                .setScaleRules(trackScaleRules)
                .addTexture(0, 0.3f)
                .build()
        );

        setKnobTexturePack(MGuiTexturePack.Builder
                .create(CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE, knobPos, knobSize)
                .addTexture(0)
                .addTexture(1, HOVERED)
                .addTexture(2, PRESSED)
                .build()
        );
    }
}