/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc.cache;

import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.MGuiPanel;
import ru.mousecray.mouseproject.api.client.gui.MGuiScreen;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.AnchorPos;
import ru.mousecray.mouseproject.api.client.gui.dim.layout.GuiMargin;
import ru.mousecray.mouseproject.core.MouseProject;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GuiCacheBuilder<T extends MGuiElement<?>, D extends MGuiPanel<?>, M extends GuiCacheBuilder<T, D, M>> {
    private WeakReference<MGuiScreen> screen;
    private WeakReference<D>          parent;
    private String                    key;
    private Supplier<T>               obj;

    private Consumer<T> createAction, existAction, finalAction;
    private GuiMargin margin = GuiMargin.ZERO();
    private AnchorPos anchor = AnchorPos.TOP_LEFT;
    private GuiVector offset = GuiVector.ZERO;

    @SuppressWarnings("unchecked") protected M self()                              { return (M) this; }

    public static <D extends MGuiPanel<?>> GuiCacheInitiator<D> create(String key) { return new GuiCacheInitiator<>(key); }

    public M setCreateAction(Consumer<T> createAction) {
        this.createAction = createAction;
        return self();
    }

    public M setExistAction(Consumer<T> existAction) {
        this.existAction = existAction;
        return self();
    }

    public M setFinalAction(Consumer<T> finalAction) {
        this.finalAction = finalAction;
        return self();
    }

    public M setMargin(GuiMargin margin) {
        this.margin = margin;
        return self();
    }

    public M setAnchor(AnchorPos anchor) {
        this.anchor = anchor;
        return self();
    }

    public M setOffset(GuiVector offset) {
        this.offset = offset;
        return self();
    }

    void setObj(Supplier<T> obj)                     { this.obj = Objects.requireNonNull(obj); }
    void setParent(WeakReference<D> parent)          { this.parent = parent; }
    void setScreen(WeakReference<MGuiScreen> screen) { this.screen = Objects.requireNonNull(screen); }
    void setKey(String key)                          { this.key = Objects.requireNonNull(key); }

    protected abstract void setObjToParent(D parent, T obj, GuiMargin margin, AnchorPos anchor, GuiVector offset);

    public M setMeasure(GuiMargin margin, AnchorPos anchor, GuiVector offset) {
        this.margin = margin;
        this.anchor = anchor;
        this.offset = offset;
        return self();
    }

    public T build() {
        MGuiScreen scr = screen.get();
        if (scr != null) {
            T element = GuiElementCache.INSTANCE.getOrCreateCEF(
                    scr, key, obj, createAction, existAction, finalAction
            );

            element.setScreen(scr);

            if (parent != null) {
                D par = parent.get();
                if (par != null) setObjToParent(par, element, margin, anchor, offset);
                else scr.addChild(element, margin, anchor, offset);
            } else scr.addChild(element, margin, anchor, offset);
            return element;
        } else {
            MouseProject.LOGGER.warn("Attempt to create cache element for non-existent MGuiScreen");
        }
        throw new IllegalStateException("Error when creating cache element for non-existent MGuiScreen");
    }
}