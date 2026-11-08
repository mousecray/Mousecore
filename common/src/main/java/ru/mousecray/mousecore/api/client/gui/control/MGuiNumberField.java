/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.control;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.texture.MGuiTexturePack;
import ru.mousecray.mouseproject.api.client.gui.control.base.MGuiBaseTextField;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.event.GuiTextTypedEvent;
import ru.mousecray.mouseproject.api.client.gui.misc.NumberMode;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES;
import static ru.mousecray.mouseproject.api.utils.MouseStaticData.CONTROLS_TEXTURES_SIZE;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MGuiNumberField extends MGuiBaseTextField<MGuiNumberField> {
    private NumberMode numberMode;

    public MGuiNumberField(GuiShape shape, MGuiString placeholder, NumberMode numberMode) {
        super(shape, placeholder);
        this.numberMode = Objects.requireNonNull(numberMode);
        setTexturePack(MGuiTexturePack.Builder
                .create(
                        CONTROLS_TEXTURES, CONTROLS_TEXTURES_SIZE,
                        GuiVector.of(104, 0), GuiVector.of(80, 10)
                )
                .addTexture(0)
                .build());
    }

    public void setNumberMode(NumberMode numberMode) { this.numberMode = Objects.requireNonNull(numberMode); }
    public NumberMode getNumberMode()                { return numberMode; }

    @Override
    protected void onTextTyped(GuiTextTypedEvent<MGuiNumberField> event) {
        String newText = event.getNewText();

        if (newText == null || newText.isEmpty()) {
            super.onTextTyped(event);
            return;
        }

        if (newText.equals("-")) {
            if (numberMode == NumberMode.POSITIVE || numberMode == NumberMode.POSITIVE_OR_ZERO) event.setCancelled(true);
            else super.onTextTyped(event);
            return;
        }

        if (!newText.matches("-?\\d+")) {
            event.setCancelled(true);
            return;
        }

        try {
            long val = Long.parseLong(newText);

            if (val > 0 && (numberMode == NumberMode.NEGATIVE || numberMode == NumberMode.NEGATIVE_OR_ZERO))
                event.setCancelled(true);
            if (val < 0 && (numberMode == NumberMode.POSITIVE || numberMode == NumberMode.POSITIVE_OR_ZERO))
                event.setCancelled(true);
            if (val == 0 && (numberMode == NumberMode.POSITIVE || numberMode == NumberMode.NEGATIVE)) event.setCancelled(true);

        } catch (NumberFormatException ex) {
            event.setCancelled(true);
            return;
        }

        if (!event.isCancelled()) super.onTextTyped(event);
    }

    public long getNumberText() {
        String text = getText();
        if (StringUtils.isEmpty(text) || text.equals("-")) return 0;
        return Long.parseLong(text);
    }

    public void setNumberText(long value) { setText(String.valueOf(value)); }
}