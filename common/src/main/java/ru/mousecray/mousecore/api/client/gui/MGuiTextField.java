/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import ru.mousecray.mouseproject.api.client.gui.component.MGuiRenderHelper;
import ru.mousecray.mouseproject.api.client.gui.component.color.MColorBuffer;
import ru.mousecray.mouseproject.api.client.gui.component.color.MGuiColorPack;
import ru.mousecray.mouseproject.api.client.gui.component.lang.MGuiString;
import ru.mousecray.mouseproject.api.client.gui.component.sound.MSoundSourceType;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiShape;
import ru.mousecray.mouseproject.api.client.gui.dim.MutableGuiVector;
import ru.mousecray.mouseproject.api.client.gui.event.*;
import ru.mousecray.mouseproject.api.client.gui.misc.FontSize;
import ru.mousecray.mouseproject.api.client.gui.misc.MoveDirection;
import ru.mousecray.mouseproject.core.MouseProject;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static ru.mousecray.mouseproject.api.client.gui.event.GuiEventFactory.pushMouseClickEvent;
import static ru.mousecray.mouseproject.api.client.gui.event.GuiEventFactory.pushTextTypedEvent;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MGuiTextField<T extends MGuiTextField<T>> extends GuiTextField implements MGuiElement<T> {
    protected final MGuiElementCore<T>   core;
    private final   GuiTextTypedEvent<T> textTypedEvent = new GuiTextTypedEvent<>();

    protected MGuiString placeholder = MGuiString.EMPTY();

    protected MGuiColorPack placeholderColorPack = MGuiColorPack.TEXT_FIELD_PLACEHOLDER();
    protected MGuiColorPack cursorColorPack      = MGuiColorPack.TEXT_FIELD_CURSOR();
    protected MGuiColorPack selectionColorPack   = MGuiColorPack.TEXT_FIELD_SELECTION();

    private boolean hasSelection = false;

    public MGuiTextField(GuiShape shape) {
        super(
                0, Minecraft.getMinecraft().fontRenderer, (int) shape.x(), (int) shape.y(),
                (int) shape.width(), (int) shape.height()
        );
        core = new MGuiElementCore<>(shape);
        core.bindEvents(Minecraft.getMinecraft(), self());
        textTypedEvent.bind(Minecraft.getMinecraft(), self());

        setColorPack(MGuiColorPack.TEXT_FIELD_SIMPLE());

        getStateManager().setChangeListener(() -> super.setFocused(getStateManager().has(MGuiElementState.FOCUSED)));

        super.setEnableBackgroundDrawing(true);
    }

    @SuppressWarnings("unchecked") @Override public T self() { return (T) this; }
    @Override public MGuiElementCore<T> getCore()            { return core; }
    @Override public void setId(int id)                      { this.id = id; }
    @Override public int getId()                             { return id; }

    @Override
    public void setGuiString(MGuiString guiString) {
        Objects.requireNonNull(guiString, "guiString cannot be null. Use MGuiString.EMPTY() instead.");
        String oldText      = getText();
        int    oldCursor    = getCursorPosition();
        int    oldSelection = getSelectionEnd();

        GuiMouseMoveEvent<T> moveEvent = getCore().getMoveEvent();
        pushTextTypedEvent(
                textTypedEvent, moveEvent.getMouseX(), moveEvent.getMouseY(),
                getCursorPosition(), getSelectionEnd(), oldText, guiString.get()
        );
        onAnyEventFire(textTypedEvent);

        if (!textTypedEvent.isCancelled()) {
            getCore().setGuiString(guiString);
            super.setText(guiString.get());
        } else {
            super.setText(oldText);
            setCursorPosition(oldCursor);
            setSelectionPos(oldSelection);
        }
    }

    public String getPlaceholder()                           { return placeholder.get(); }
    public void setPlaceholder(@Nullable String placeholder) { this.placeholder = MGuiString.simple(placeholder); }

    public void setPlaceholder(MGuiString placeholder) {
        Objects.requireNonNull(placeholder, "placeholder cannot be null. Use MGuiString.EMPTY() instead.");
        this.placeholder = placeholder;
    }

    public boolean isHasSelection()                { return hasSelection; }

    public MGuiColorPack getPlaceholderColorPack() { return placeholderColorPack; }

    public void setPlaceholderColorPack(MGuiColorPack colorPack) {
        Objects.requireNonNull(colorPack, "colorPack cannot be null. Use MGuiColorPack.EMPTY() instead.");
        placeholderColorPack = colorPack;
    }

    public MGuiColorPack getCursorColorPack() { return cursorColorPack; }

    public void setCursorColorPack(MGuiColorPack colorPack) {
        Objects.requireNonNull(colorPack, "colorPack cannot be null. Use MGuiColorPack.EMPTY() instead.");
        cursorColorPack = colorPack;
    }

    public MGuiColorPack getSelectionColorPack() { return selectionColorPack; }

    public void setSelectionColorPack(MGuiColorPack colorPack) {
        Objects.requireNonNull(colorPack, "colorPack cannot be null. Use MGuiColorPack.EMPTY() instead.");
        selectionColorPack = colorPack;
    }

    @Override
    public void setupShapeToVanilla(IGuiShape result) {
        x = (int) result.x();
        y = (int) result.y();
        width = (int) result.width();
        height = (int) result.height();
    }

    //Диспетчеризация событий
    @Override public final boolean dispatchMousePressed(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
        if (!getCalculatedShape().contains(mouseX, mouseY)) return false;
        if (!isEnabled() || !isVisible()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DISABLED);
            return false;
        }

        if (getStateManager().has(MGuiElementState.FAIL)) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.FAIL);
            return false;
        }

        hasSelection = checkIsOnText(mouseX, mouseY);
        if (hasSelection && mouseButton == 0) setCursorPosition(getCharIndexAtMouse(mouseX));

        getCore().setTickDown(0);
        getStateManager().add(MGuiElementState.PRESSED);

        GuiMouseClickEvent<T> pressEvent = getCore().getPressEvent();
        pushMouseClickEvent(pressEvent, mouseX, mouseY);
        onAnyEventFire(pressEvent);
        if (!pressEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.PRESS);
            onMousePressed(pressEvent);
        }
        return true;
    }

    @Override
    public final void dispatchMouseReleased(Minecraft mc, int mouseX, int mouseY, int state) {
        getCore().setTickDown(-1);
        hasSelection = false;
        getStateManager().remove(MGuiElementState.PRESSED);

        GuiMouseClickEvent<T> releaseEvent = getCore().getReleaseEvent();
        pushMouseClickEvent(releaseEvent, mouseX, mouseY);
        onAnyEventFire(releaseEvent);

        if (!releaseEvent.isCancelled()) {
            dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.RELEASE);
            onMouseReleased(releaseEvent);
            if (getCalculatedShape().contains(mouseX, mouseY)) {
                GuiMouseClickEvent<T> clickEvent = getCore().getClickEvent();
                pushMouseClickEvent(clickEvent, mouseX, mouseY);
                onAnyEventFire(clickEvent);
                if (!clickEvent.isCancelled()) {
                    dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.CLICK);
                    onClick(clickEvent);
                }
            }
        }
    }

    @Override
    public final boolean dispatchMouseDragged(Minecraft mc, int mouseX, int mouseY, MoveDirection dir, int diffX, int diffY) {
        GuiMouseDragEvent<T> dragEvent = getCore().getDragEvent();
        int                  tickDown  = getCore().getTickDown();
        if (tickDown >= 0) {
            GuiEventFactory.pushMouseDragEvent(dragEvent, mouseX, mouseY, dir, diffX, diffY, tickDown);
            onAnyEventFire(dragEvent);

            if (!dragEvent.isCancelled()) {
                if (hasSelection) {
                    setSelectionPos(getCharIndexAtMouse(mouseX));
                    dragEvent.consume();
                }

                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.DRAG);
                onMouseDragged(dragEvent);
            }
            return dragEvent.isConsumed();
        }
        return false;
    }

    @Override
    public final boolean dispatchKeyTyped(Minecraft mc, int mouseX, int mouseY, char typedChar, int keyCode) {
        if (!isFocused() || !isVisible()) return false;

        String oldText      = getText();
        int    oldCursor    = getCursorPosition();
        int    oldSelection = getSelectionEnd();

        int   oldW  = width;
        float scale = getFontSize().getScale() * getTextScaleMultiplayer();
        width = (int) (getCalculatedInnerShape().width() / scale);

        boolean handled = super.textboxKeyTyped(typedChar, keyCode);

        width = oldW;

        if (handled) {
            if (!oldText.equals(getText())) {
                pushTextTypedEvent(
                        textTypedEvent, mouseX, mouseY, getCursorPosition(), getSelectionEnd(), oldText, getText()
                );
                onAnyEventFire(textTypedEvent);
                if (!textTypedEvent.isCancelled()) onTextTyped(textTypedEvent);

                if (textTypedEvent.isCancelled()) {
                    super.setText(oldText);
                    setCursorPosition(oldCursor);
                    setSelectionPos(oldSelection);
                } else {
                    if (!textTypedEvent.isConsumed()) getCore().setGuiString(MGuiString.simple(getText()));
                }
            }

            GuiKeyEvent<T> keyEvent = getCore().getKeyEvent();
            GuiEventFactory.pushKeyEvent(keyEvent, mouseX, mouseY, typedChar, keyCode);
            onAnyEventFire(keyEvent);
            if (!keyEvent.isCancelled()) {
                dispatchPlaySound(mc, mc.getSoundHandler(), MSoundSourceType.KEY_TYPED);
                onKeyTyped(keyEvent);
            }
            return keyEvent.isConsumed();
        }

        return false;
    }


    @Override
    public void onDrawText(GuiTickEvent<T> event) {
        if (getText().isEmpty() && !isFocused() && placeholder.get() != null && !placeholder.get().isEmpty()) {
            FontRenderer fr     = getFontRenderer();
            int          pColor = placeholderColorPack.getCalculatedColor(getStateManager());

            FontSize fs           = getFontSize();
            float    scale        = fs.getScale() * getTextScaleMultiplayer();
            float    inverseScale = 1.0F / scale;

            MutableGuiShape  calculatedInnerShape = getCalculatedInnerShape();
            float            innerX               = calculatedInnerShape.x();
            float            innerY               = calculatedInnerShape.y();
            float            innerH               = calculatedInnerShape.height();
            MutableGuiVector offset               = getCore().getCalculatedTextOffsetTemp();

            float logicalX = (innerX + offset.x()) * inverseScale;
            float logicalY = (innerY + innerH / 2f + offset.y()) * inverseScale - (fr.FONT_HEIGHT / 2f) * inverseScale;

            float textX = Math.round(logicalX * scale) / scale;
            float textY = Math.round(logicalY * scale) / scale;

            GlStateManager.pushMatrix();
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GlStateManager.scale(scale, scale, 1.0F);

            MGuiRenderHelper.drawString(fr, placeholder.get(), textX, textY, pColor, fs != FontSize.SMALL);

            GlStateManager.popMatrix();
        } else drawCustomTextLayer(event);
    }

    private void drawCustomTextLayer(GuiTickEvent<T> event) {
        float scale        = getFontSize().getScale() * getTextScaleMultiplayer();
        float inverseScale = 1.0F / scale;

        FontRenderer fontRenderer = getFontRenderer();
        int          textColor    = getColorPack().getCalculatedColor(getStateManager());
        String       fullText     = getText();

        int cursorPos       = getCursorPosition() - lineScrollOffset;
        int selectionEndPos = getSelectionEnd() - lineScrollOffset;

        MutableGuiShape  calculatedInnerShape = getCalculatedInnerShape();
        float            innerX               = calculatedInnerShape.x();
        float            innerY               = calculatedInnerShape.y();
        float            innerH               = calculatedInnerShape.height();
        MutableGuiVector offset               = getCore().getCalculatedTextOffsetTemp();


        int scaledAvailableWidth = (int) (calculatedInnerShape.width() * inverseScale);

        float logicalX = (innerX + offset.x()) * inverseScale;
        float logicalY = (innerY + innerH / 2f + offset.y()) * inverseScale - (fontRenderer.FONT_HEIGHT / 2f) * inverseScale;

        float textX = Math.round(logicalX * scale) / scale;
        float textY = Math.round(logicalY * scale) / scale;

        GlStateManager.pushMatrix();
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.scale(scale, scale, 1.0F);

        String visibleText = fontRenderer.trimStringToWidth(fullText.substring(lineScrollOffset), scaledAvailableWidth);

        if (selectionEndPos < 0) selectionEndPos = 0;
        if (selectionEndPos > visibleText.length()) selectionEndPos = visibleText.length();

        boolean showCursor    = isFocused() && cursorCounter / 6 % 2 == 0 && cursorPos >= 0 && cursorPos <= visibleText.length();
        boolean isCursorAtEnd = getCursorPosition() < fullText.length() || fullText.length() >= getMaxStringLength();

        float  cursorX          = textX;
        String textBeforeCursor = null;
        if (!visibleText.isEmpty()) textBeforeCursor = cursorPos >= 0 && cursorPos <= visibleText.length()
                ? visibleText.substring(0, cursorPos) : visibleText;

        if (!visibleText.isEmpty()) cursorX = textX + fontRenderer.getStringWidth(textBeforeCursor);

        if (cursorPos < 0) cursorX = textX;
        else if (cursorPos > visibleText.length()) cursorX = textX + (float) scaledAvailableWidth;
        else if (isCursorAtEnd) cursorX--;

        FontSize fontSize = getFontSize();
        if (!visibleText.isEmpty()) {
            float currentX = MGuiRenderHelper.drawString(
                    fontRenderer, textBeforeCursor, textX, textY, textColor, fontSize != FontSize.SMALL
            );

            if (cursorPos >= 0 && cursorPos < visibleText.length()) MGuiRenderHelper.drawString(
                    fontRenderer, visibleText.substring(cursorPos), currentX,
                    textY, textColor, fontSize != FontSize.SMALL
            );
        }

        if (showCursor) {
            int cursorColor = cursorColorPack.getCalculatedColor(getStateManager());
            if (isCursorAtEnd) MGuiRenderHelper.drawRect(
                    cursorX, textY - 1, cursorX + 1, textY + 1 + fontRenderer.FONT_HEIGHT, cursorColor
            );
            else MGuiRenderHelper.drawString(
                    fontRenderer, "|", cursorX, textY, cursorColor, fontSize != FontSize.SMALL
            );
        }

        if (selectionEndPos != cursorPos) {
            float selectionEndX = textX + fontRenderer.getStringWidth(visibleText.substring(0, selectionEndPos));
            float maxX          = textX + (float) scaledAvailableWidth;
            drawSelectionBox(
                    cursorX, textY - 1, selectionEndX - 1,
                    textY + 1 + fontRenderer.FONT_HEIGHT, textX, maxX
            );
        }

        GlStateManager.popMatrix();
    }

    private void drawSelectionBox(float startX, float startY, float endX, float endY, float minX, float maxX) {
        if (startX < endX) {
            float i = startX;
            startX = endX;
            endX = i;
        }
        if (startY < endY) {
            float j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > maxX) endX = maxX;
        if (startX > maxX) startX = maxX;
        if (endX < minX) endX = minX;
        if (startX < minX) startX = minX;

        Tessellator   tessellator   = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        selectionColorPack.intToColor(getStateManager());
        MColorBuffer buf = selectionColorPack.getColorBuffer();
        GlStateManager.color(buf.getRed(), buf.getGreen(), buf.getBlue(), buf.getAlpha());
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(startX, endY, 0.0D).endVertex();
        bufferbuilder.pos(endX, endY, 0.0D).endVertex();
        bufferbuilder.pos(endX, startY, 0.0D).endVertex();
        bufferbuilder.pos(startX, startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onUpdate(GuiTickEvent<T> event) {
        MGuiElement.super.onUpdate(event);
        updateCursorCounter();
    }

    @Override
    public void onKeyTyped(GuiKeyEvent<T> event) {
        if (!event.isCancelled() && (event.getKeyCode() == Keyboard.KEY_RETURN || event.getKeyCode() == Keyboard.KEY_NUMPADENTER)) {
            dispatchMousePressed(event.getMc(), x + width / 2, y + height / 2, 0);
            dispatchMouseReleased(event.getMc(), x + width / 2, y + height / 2, 0);
            event.consume();
        }
    }

    protected void onTextTyped(GuiTextTypedEvent<T> event) { }

    protected boolean checkIsOnText(int mouseX, int mouseY) {
        MutableGuiShape calculatedInnerShape = getCalculatedInnerShape();
        FontRenderer    fr                   = getFontRenderer();
        float           scale                = getFontSize().getScale();

        float centerY    = calculatedInnerShape.y() + calculatedInnerShape.height() / 2f + getCore().getCalculatedTextOffsetTemp().y();
        float halfHeight = (fr.FONT_HEIGHT + 2) * scale / 2f;

        float textStartX = calculatedInnerShape.x() + getCore().getCalculatedTextOffsetTemp().x();
        float textEndX   = calculatedInnerShape.x() + calculatedInnerShape.width();

        return mouseX >= textStartX && mouseX <= textEndX && mouseY >= centerY - halfHeight && mouseY <= centerY + halfHeight;
    }

    protected int getCharIndexAtMouse(int mouseX) {
        FontRenderer fr           = getFontRenderer();
        float        scale        = getFontSize().getScale() * getTextScaleMultiplayer();
        float        inverseScale = 1.0f / scale;

        MutableGuiShape calculatedInnerShape = getCalculatedInnerShape();
        float           textX                = calculatedInnerShape.x() + getCore().getCalculatedTextOffsetTemp().x();
        int             relX                 = (int) ((mouseX - textX) * inverseScale);

        String visibleText = fr.trimStringToWidth(
                getText().substring(lineScrollOffset),
                (int) (calculatedInnerShape.width() * inverseScale)
        );
        return fr.trimStringToWidth(visibleText, relX).length() + lineScrollOffset;
    }

    //Интеграция с vanilla
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return dispatchMousePressed(Minecraft.getMinecraft(), mouseX, mouseY, mouseButton);
    }
    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        GuiMouseMoveEvent<T> moveEvent = getCore().getMoveEvent();
        return dispatchKeyTyped(Minecraft.getMinecraft(), moveEvent.getMouseX(), moveEvent.getMouseY(), typedChar, keyCode);
    }
    @Override
    public final void drawTextBox() {
        GuiMouseMoveEvent<T> moveEvent = getCore().getMoveEvent();
        dispatchDraw(
                Minecraft.getMinecraft(), moveEvent.getMouseX(), moveEvent.getMouseY(),
                Minecraft.getMinecraft().getRenderPartialTicks()
        );
    }
    @Override
    public boolean mouseHover(Minecraft mc, int mouseX, int mouseY) {
        return MGuiElement.super.mouseHover(mc, mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return MGuiElement.super.mousePressed(mc, mouseX, mouseY);
    }

    @Override public final int getHoverState(boolean mouseOver)           { return MGuiElement.super.getHoverState(mouseOver); }
    @Override public void mouseReleased(int mouseX, int mouseY)           { MGuiElement.super.mouseReleased(mouseX, mouseY); }
    @Override public final void playPressSound(SoundHandler soundHandler) { MGuiElement.super.playPressSound(soundHandler); }
    @Override public boolean isMouseOver()                                { return MGuiElement.super.isMouseOver(); }

    @Override
    public final void setEnableBackgroundDrawing(boolean enableBackgroundDrawing) {
        MouseProject.LOGGER.warn("backgroundDrawing is permanently enabled for MGuiTextField. " +
                "If you are attempting to set it manually, please keep in mind that doing so will have no effect.");
    }

    @Override
    public void writeText(String textToWrite) {
        GuiMouseMoveEvent<T> moveEvent = getCore().getMoveEvent();
        String               newText   = internalWriteText(textToWrite);
        pushTextTypedEvent(
                textTypedEvent, moveEvent.getMouseX(), moveEvent.getMouseY(),
                getCursorPosition(), getSelectionEnd(), getText(), newText
        );
        onAnyEventFire(textTypedEvent);
        if (!textTypedEvent.isCancelled()) {
            getCore().setGuiString(MGuiString.simple(newText));
            super.writeText(textToWrite);
        }
    }

    @Override
    public void deleteFromCursor(int num) {
        GuiMouseMoveEvent<T> moveEvent = getCore().getMoveEvent();
        String               newText   = internalDeleteFromCursor(num);
        pushTextTypedEvent(
                textTypedEvent, moveEvent.getMouseX(), moveEvent.getMouseY(), getCursorPosition(), getSelectionEnd(),
                getText(), newText
        );
        onAnyEventFire(textTypedEvent);
        if (!textTypedEvent.isCancelled()) {
            getCore().setGuiString(MGuiString.simple(newText));
            super.deleteFromCursor(num);
        }
    }

    private String internalWriteText(String textToWrite) {
        String s  = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int    i  = Math.min(getCursorPosition(), getSelectionEnd());
        int    j  = Math.max(getCursorPosition(), getSelectionEnd());
        int    k  = getMaxStringLength() - getText().length() - (i - j);

        if (!getText().isEmpty()) s = s + getText().substring(0, i);
        if (k < s1.length()) s = s + s1.substring(0, k);
        else s = s + s1;
        if (!getText().isEmpty() && j < getText().length()) s = s + getText().substring(j);

        return s;
    }

    private String internalDeleteFromCursor(int num) {
        if (!getText().isEmpty()) {
            if (getSelectionEnd() != getCursorPosition()) return internalWriteText("");
            else {
                boolean flag = num < 0;
                int     i    = flag ? getCursorPosition() + num : getCursorPosition();
                int     j    = flag ? getCursorPosition() : getCursorPosition() + num;
                String  s    = "";

                if (i >= 0) s = getText().substring(0, i);
                if (j < getText().length()) s = s + getText().substring(j);
                return s;
            }
        }
        return "";
    }

    @Override
    public void setSelectionPos(int position) {
        int textLength = getText().length();

        if (position > textLength) position = textLength;
        if (position < 0) position = 0;

        selectionEnd = position;

        FontRenderer fr = getFontRenderer();
        if (lineScrollOffset > textLength) {
            lineScrollOffset = textLength;
        }

        float scale                = getFontSize().getScale() * getTextScaleMultiplayer();
        int   scaledAvailableWidth = (int) (getCalculatedInnerShape().width() / scale);

        String s = fr.trimStringToWidth(getText().substring(lineScrollOffset), scaledAvailableWidth);
        int    k = s.length() + lineScrollOffset;

        if (position == lineScrollOffset) {
            lineScrollOffset -= fr.trimStringToWidth(getText(), scaledAvailableWidth, true).length();
        }

        if (position > k) lineScrollOffset += position - k;
        else if (position <= lineScrollOffset) lineScrollOffset -= lineScrollOffset - position;

        lineScrollOffset = net.minecraft.util.math.MathHelper.clamp(lineScrollOffset, 0, textLength);
    }

    @Override public boolean getVisible() { return isVisible(); }

    @Override
    public void setVisible(boolean isVisible) {
        if (isVisible) getStateManager().remove(MGuiElementState.HIDDEN);
        else getStateManager().add(MGuiElementState.HIDDEN);
    }
}
