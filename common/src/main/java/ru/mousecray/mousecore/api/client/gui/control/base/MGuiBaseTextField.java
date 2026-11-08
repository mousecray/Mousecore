/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control.base;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiTextField;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.event.GuiTextTypedEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MGuiBaseTextField<T extends MGuiBaseTextField<T>> extends MGuiTextField<T> {
    private Consumer<GuiTextTypedEvent<T>> onTextTypedListener;

    public MGuiBaseTextField(GuiShape shape, MGuiString placeholder) {
        super(shape);
        setPlaceholder(placeholder);
    }

    public void setOnTextTypedListener(@Nullable Consumer<GuiTextTypedEvent<T>> listener) { onTextTypedListener = listener; }
    public Consumer<GuiTextTypedEvent<T>> getOnTextTypedListener()                        { return onTextTypedListener; }

    @Override
    protected void onTextTyped(GuiTextTypedEvent<T> event) {
        super.onTextTyped(event);
        if (onTextTypedListener != null && !event.isCancelled()) onTextTypedListener.accept(event);
    }
}