/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.misc.cache;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.MGuiElement;
import ru.mousecray.mouseproject.api.client.gui.MGuiScreen;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class GuiElementCache {
    public static final GuiElementCache INSTANCE = new GuiElementCache();

    private final Map<String, SoftReference<MGuiElement<?>>> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends MGuiElement<?>> T get(MGuiScreen screen, String key) {
        SoftReference<MGuiElement<?>> ref = cache.get(screen.getScreenName() + ":" + key);
        return ref != null ? (T) ref.get() : null;
    }

    public <T extends MGuiElement<?>> T getOrCreateCEF(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction, Consumer<T> existAction, Consumer<T> finalAction) {
        T t = get(screen, key);
        if (t == null) {
            put(screen, key, t = typeSupplier.get());
            if (createAction != null) createAction.accept(t);
        } else if (existAction != null) existAction.accept(t);
        if (finalAction != null) finalAction.accept(t);
        return t;
    }

    public <T extends MGuiElement<?>> T getOrCreateCF(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction, Consumer<T> finalAction) {
        return getOrCreateCEF(screen, key, typeSupplier, createAction, null, finalAction);
    }

    public <T extends MGuiElement<?>> T getOrCreateEF(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> existAction, Consumer<T> finalAction) {
        return getOrCreateCEF(screen, key, typeSupplier, null, existAction, finalAction);
    }

    public <T extends MGuiElement<?>> T getOrCreateCE(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction, Consumer<T> existAction) {
        return getOrCreateCEF(screen, key, typeSupplier, createAction, existAction, null);
    }

    public <T extends MGuiElement<?>> T getOrCreateC(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> createAction) {
        return getOrCreateCEF(screen, key, typeSupplier, createAction, null, null);
    }

    public <T extends MGuiElement<?>> T getOrCreateE(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> existAction) {
        return getOrCreateCEF(screen, key, typeSupplier, null, existAction, null);
    }

    public <T extends MGuiElement<?>> T getOrCreateF(MGuiScreen screen, String key, Supplier<T> typeSupplier, Consumer<T> finalAction) {
        return getOrCreateCEF(screen, key, typeSupplier, null, null, finalAction);
    }

    public <T extends MGuiElement<?>> T getOrCreate(MGuiScreen screen, String key, Supplier<T> typeSupplier) {
        return getOrCreateCEF(screen, key, typeSupplier, null, null, null);
    }

    public <T extends MGuiElement<?>> void put(MGuiScreen screen, String key, T element) {
        cache.put(screen.getScreenName() + ":" + key, new SoftReference<>(element));
    }

    public void clear() {
        cache.clear();
    }
}
