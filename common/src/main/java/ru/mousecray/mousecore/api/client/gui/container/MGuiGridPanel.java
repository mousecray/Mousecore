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
import ru.mousecray.mouseproject.api.client.gui.dim.*;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MGuiGridPanel extends MGuiPanel<MGuiGridPanel> {
    private static final GridPos GRID_POS_ZERO = new GridPos(0, 0);

    private int   gridRows;
    private int   gridCols;
    private float gridGapX = 0f;
    private float gridGapY = 0f;

    public MGuiGridPanel(GuiShape elementShape, int rows, int cols) {
        super(elementShape);
        gridRows = Math.max(1, rows);
        gridCols = Math.max(1, cols);
    }

    public MGuiGridPanel setGridSize(int rows, int cols) {
        gridRows = Math.max(1, rows);
        gridCols = Math.max(1, cols);
        return this;
    }

    public MGuiGridPanel setGaps(float gapX, float gapY) {
        gridGapX = gapX;
        gridGapY = gapY;
        return this;
    }

    @Override
    public void measurePreferred(IGuiVector pDefSize, IGuiVector pContentSize, float sugX, float sugY, MutableGuiVector result) {
        super.measurePreferred(pDefSize, pContentSize, sugX, sugY, result);
        GuiScaleRules rules = getScaleRules();
        if (!rules.isWrapHorizontal() && !rules.isWrapVertical()) return;

        float scaledGapX = calculateFlowComponentX(pDefSize, pContentSize, gridGapX);
        float scaledGapY = calculateFlowComponentY(pDefSize, pContentSize, gridGapY);

        float maxCellW = 0;
        float maxCellH = 0;

        for (MGuiElement<?> child : children) {
            measureChildWithMargin(pDefSize, pContentSize, sugX, sugY, child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            GuiLayoutParams params = child.getCore().getLayoutParams();
            GridPos         pos    = params instanceof GridLayoutParams ? ((GridLayoutParams) params).gridPos : GridPos.DEFAULT();

            float childReqW = measureTemp.x() + ml + mr;
            float childReqH = measureTemp.y() + mt + mb;

            float cellW = (childReqW - scaledGapX * (pos.colSpan - 1)) / pos.colSpan;
            float cellH = (childReqH - scaledGapY * (pos.rowSpan - 1)) / pos.rowSpan;

            if (cellW > maxCellW) maxCellW = cellW;
            if (cellH > maxCellH) maxCellH = cellH;
        }

        if (rules.isWrapHorizontal()) {
            float      totalW = maxCellW * gridCols + scaledGapX * Math.max(0, gridCols - 1);
            GuiPadding pad    = getPadding();
            totalW += calculateFlowComponentX(pDefSize, pContentSize, pad.getLeft() + pad.getRight());
            result.withX(totalW);
        }
        if (rules.isWrapVertical()) {
            float      totalH = maxCellH * gridRows + scaledGapY * Math.max(0, gridRows - 1);
            GuiPadding pad    = getPadding();
            totalH += calculateFlowComponentY(pDefSize, pContentSize, pad.getTop() + pad.getBottom());
            result.withY(totalH);
        }
    }

    public void addChild(MGuiElement<?> child, @Nullable GuiMargin margin, @Nullable AnchorPos anchor, @Nullable GuiVector offset, @Nullable GridPos gridPos) {
        super.addChild(child, margin, anchor, offset);

        child.getCore().setLayoutParams(new GridLayoutParams(margin, anchor, offset, gridPos));
    }

    @Override
    protected void layoutChildren(IGuiVector parentDefaultSize, IGuiVector parentContentSize, MutableGuiShape inner) {
        if (gridRows <= 0 || gridCols <= 0) return;

        float scaledGapX = calculateFlowComponentX(parentDefaultSize, parentContentSize, gridGapX);
        float scaledGapY = calculateFlowComponentY(parentDefaultSize, parentContentSize, gridGapY);

        float availW = inner.width() - scaledGapX * (gridCols - 1);
        float availH = inner.height() - scaledGapY * (gridRows - 1);

        float cellW = Math.max(0, availW / gridCols);
        float cellH = Math.max(0, availH / gridRows);

        for (MGuiElement<?> child : children) {
            GuiLayoutParams params = child.getCore().getLayoutParams();

            AnchorPos anchor = params.anchor;

            GridPos pos = params instanceof GridLayoutParams ? ((GridLayoutParams) params).gridPos : GridPos.DEFAULT();

            float cellAreaX = inner.x() + pos.col * (cellW + scaledGapX);
            float cellAreaY = inner.y() + pos.row * (cellH + scaledGapY);
            float cellAreaW = cellW * pos.colSpan + scaledGapX * (pos.colSpan - 1);
            float cellAreaH = cellH * pos.rowSpan + scaledGapY * (pos.rowSpan - 1);

            measureChildWithMargin(parentDefaultSize, parentContentSize, cellAreaW, cellAreaH, child, getChildMargin(child), marginTemp, measureTemp);
            float ml = marginTemp[0], mt = marginTemp[1], mr = marginTemp[2], mb = marginTemp[3];

            float childAvailW = Math.max(0, cellAreaW - ml - mr);
            float childAvailH = Math.max(0, cellAreaH - mt - mb);

            child.measurePreferred(parentDefaultSize, parentContentSize, childAvailW, childAvailH, measureTemp);
            float childW = measureTemp.x();
            float childH = measureTemp.y();

            float childX = cellAreaX + ml;
            float childY = cellAreaY + mt;

            GuiVector offset  = getChildOffset(child);
            float     offsetX = calculateFlowComponentX(parentDefaultSize, parentContentSize, offset.x());
            float     offsetY = calculateFlowComponentY(parentDefaultSize, parentContentSize, offset.y());

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
            child.calculate(parentDefaultSize, parentContentSize, childAvailableTemp);
        }
    }
}