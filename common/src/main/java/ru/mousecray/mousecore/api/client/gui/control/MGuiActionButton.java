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

import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.*;
import static ru.mousecray.mouseproject.api.client.gui.dim.GuiVector.ZERO;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class MGuiActionButton extends MGuiBaseButton<MGuiActionButton> {
    public MGuiActionButton(
            GuiShape shape, MGuiString text
    ) {
        super(shape, text);
        setTexturePack(MGuiTexturePack.Builder
                .create(
                        CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE,
                        ZERO, GuiVector.of(80, 10)
                )
                .addTexture(0, DISABLED)
                .addTexture(1)
                .addTexture(2, HOVERED)
                .addTexture(3, PRESSED)
                .addTexture(4, FAIL)
                .build());
    }
}
