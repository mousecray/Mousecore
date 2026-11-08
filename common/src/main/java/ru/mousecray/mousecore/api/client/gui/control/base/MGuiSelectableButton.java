/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control.base;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseClickEvent;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState.SELECTED;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public abstract class MGuiSelectableButton<T extends MGuiSelectableButton<T>> extends MGuiBaseButton<T> {
    public MGuiSelectableButton(GuiShape shape, MGuiString text) {
        super(shape, text);
    }

    @Override
    public void onClick(GuiMouseClickEvent<T> event) {
        if (getStateManager().has(SELECTED)) getStateManager().remove(SELECTED);
        else getStateManager().add(SELECTED);
        super.onClick(event);
    }
}