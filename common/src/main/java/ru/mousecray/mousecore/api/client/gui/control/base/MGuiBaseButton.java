/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control.base;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiButton;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.event.GuiMouseClickEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MGuiBaseButton<T extends MGuiBaseButton<T>> extends MGuiButton<T> {
    private Consumer<GuiMouseClickEvent<T>> onClickListener;

    public MGuiBaseButton(GuiShape shape, MGuiString text) {
        super(shape);
        setGuiString(text);
    }

    public void setOnClickListener(@Nullable Consumer<GuiMouseClickEvent<T>> listener) { onClickListener = listener; }
    public Consumer<GuiMouseClickEvent<T>> getOnClickListener()                        { return onClickListener; }

    @Override
    public void onClick(GuiMouseClickEvent<T> event) {
        if (onClickListener != null) onClickListener.accept(event);
    }
}