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
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiPadding;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiScaleRules;

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MGuiFreePanel extends MGuiPanel<MGuiFreePanel> {
    public MGuiFreePanel(GuiShape elementShape) { super(elementShape); }

    @Override
    public void measurePreferred(IGuiVector pDefSize, IGuiVector pContentSize, float sugX, float sugY, MutableGuiVector result) {
        super.measurePreferred(pDefSize, pContentSize, sugX, sugY, result);
        GuiScaleRules rules = getScaleRules();
        if (!rules.isWrapHorizontal() && !rules.isWrapVertical()) return;

        float maxR = 0;
        float maxB = 0;

        for (MGuiElement<?> child : children) {
            if (!child.isVisible()) continue;
            measureChildWithMargin(pDefSize, pContentSize, sugX, sugY, child, getChildMargin(child), marginTemp, measureTemp);

            float posX = child.getScaleRules().isFixedHorizontal() ? child.getShape().x()
                    : calculateFlowComponentX(pDefSize, pContentSize, child.getShape().x());
            float posY = child.getScaleRules().isFixedVertical() ? child.getShape().y()
                    : calculateFlowComponentY(pDefSize, pContentSize, child.getShape().y());

            float[] scaledOffset = calculateScaledOffset(child, pDefSize, pContentSize);

            float right  = posX + scaledOffset[0] + marginTemp[0] + measureTemp.x() + marginTemp[2];
            float bottom = posY + scaledOffset[1] + marginTemp[1] + measureTemp.y() + marginTemp[3];

            if (right > maxR) maxR = right;
            if (bottom > maxB) maxB = bottom;
        }

        GuiPadding pad = getPadding();
        maxR += calculateFlowComponentX(pDefSize, pContentSize, pad.getLeft() + pad.getRight());
        maxB += calculateFlowComponentY(pDefSize, pContentSize, pad.getTop() + pad.getBottom());

        if (rules.isWrapHorizontal()) result.withX(maxR);
        if (rules.isWrapVertical()) result.withY(maxB);
    }

    @Override
    protected void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MutableGuiShape inner) {
        for (MGuiElement<?> child : children) {
            if (!child.isVisible()) continue;

            measureChildWithMargin(parentDefaultSize, parentContentSize, inner.width(), inner.height(), child,
                    getChildMargin(child), marginTemp, measureTemp
            );
            float ml     = marginTemp[0], mt = marginTemp[1];
            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float posX = child.getScaleRules().isFixedHorizontal() ? child.getShape().x()
                    : calculateFlowComponentX(parentDefaultSize, parentContentSize, child.getShape().x());
            float posY = child.getScaleRules().isFixedVertical() ? child.getShape().y()
                    : calculateFlowComponentY(parentDefaultSize, parentContentSize, child.getShape().y());

            float[] scaledOffset = calculateScaledOffset(child, parentDefaultSize, parentContentSize);
            float   offsetX      = scaledOffset[0], offsetY = scaledOffset[1];

            childAvailableTemp.withX(inner.x() + ml + posX + offsetX);
            childAvailableTemp.withY(inner.y() + mt + posY + offsetY);
            childAvailableTemp.withWidth(childW);
            childAvailableTemp.withHeight(childH);

            child.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);
        }
    }
}