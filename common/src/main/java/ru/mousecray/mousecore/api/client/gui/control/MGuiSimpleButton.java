/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.control.base.MGuiBaseButton;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.HOVERED;
import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.PRESSED;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class MGuiSimpleButton extends MGuiBaseButton<MGuiSimpleButton> {
    public MGuiSimpleButton(GuiShape shape, MGuiString text) {
        super(shape, text);
        setTexturePack(MGuiTexturePack.Builder
                .create(
                        CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE,
                        GuiVector.of(80, 0), GuiVector.of(10)
                )
                .addTexture(0)
                .addTexture(1, HOVERED)
                .addTexture(2, PRESSED)
                .build());
    }
}
