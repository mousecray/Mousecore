/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.container;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.MGuiPanel;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.AnchorPos;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiLayoutParams;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MGuiAnchorPanel extends MGuiPanel<MGuiAnchorPanel> {

    public MGuiAnchorPanel(GuiShape elementShape) { super(elementShape); }

    @Override
    protected void layoutChildren(IGuiVector pDefSize, IGuiVector pContentSize, MutableGuiShape inner) {
        for (MGuiElement<?> child : children) {
            MGuiRenderHelper.measureChildWithMargin(
                    pDefSize, pContentSize, inner.width(), inner.height(), child,
                    getChildMargin(child), marginTemp, measureTemp
            );
            float ml     = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];
            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float childAvailW = Math.max(0, inner.width() - ml - mr);
            float childAvailH = Math.max(0, inner.height() - mt - mb);

            float childX = inner.x() + ml;
            float childY = inner.y() + mt;

            GuiLayoutParams params = child.getCore().getLayoutParams();

            AnchorPos anchor       = params.anchor;
            float[]   scaledOffset = calculateScaledOffset(child, pDefSize, pContentSize);
            float     offsetX      = scaledOffset[0], offsetY = scaledOffset[1];

            switch (anchor) {
                case TOP_LEFT:
                    childX += offsetX;
                    childY += offsetY;
                    break;
                case TOP_CENTER:
                    childX += (childAvailW - childW) / 2 + offsetX;
                    childY += offsetY;
                    break;
                case TOP_RIGHT:
                    childX += childAvailW - childW + offsetX;
                    childY += offsetY;
                    break;
                case MIDDLE_LEFT:
                    childX += offsetX;
                    childY += (childAvailH - childH) / 2 + offsetY;
                    break;
                case MIDDLE_CENTER:
                    childX += (childAvailW - childW) / 2 + offsetX;
                    childY += (childAvailH - childH) / 2 + offsetY;
                    break;
                case MIDDLE_RIGHT:
                    childX += childAvailW - childW + offsetX;
                    childY += (childAvailH - childH) / 2 + offsetY;
                    break;
                case BOTTOM_LEFT:
                    childX += offsetX;
                    childY += childAvailH - childH + offsetY;
                    break;
                case BOTTOM_CENTER:
                    childX += (childAvailW - childW) / 2 + offsetX;
                    childY += childAvailH - childH + offsetY;
                    break;
                case BOTTOM_RIGHT:
                    childX += childAvailW - childW + offsetX;
                    childY += childAvailH - childH + offsetY;
                    break;
            }

            childAvailableTemp.withX(childX).withY(childY).withWidth(childW).withHeight(childH);
            child.calculate(pDefSize, pContentSize, childAvailableTemp);
        }
    }
}