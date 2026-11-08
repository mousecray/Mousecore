/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.event;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class GuiEvent<T extends MGuiElement<T>> {
    private T         obj;
    private Minecraft mc;
    private int       mouseX, mouseY;
    private boolean cancelled;
    private boolean consumed = false;

    public void bind(Minecraft mc, T obj) {
        this.obj = obj;
        this.mc = mc;
    }

    void setMouseX(int mouseX)                  { this.mouseX = mouseX; }
    void setMouseY(int mouseY)                  { this.mouseY = mouseY; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public void consume()                       { consumed = true; }

    public Minecraft getMc()                    { return mc; }
    public int getMouseX()                      { return mouseX; }
    public int getMouseY()                      { return mouseY; }
    public T getObj()                           { return obj; }
    public boolean isCancelled()                { return cancelled; }
    public boolean isConsumed()                 { return consumed; }

    protected void reset() {
        cancelled = false;
        consumed = false;
    }
}