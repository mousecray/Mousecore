package ru.mousecray.mousecore.api.asm.event;

import com.google.common.collect.ImmutableList;
import ru.mousecray.mousecore.api.asm.transformer.MouseHookParent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MouseLoadEvent {
    private final List<MouseHookParent> hooks = new ArrayList<>();

    public void registerHook(@Nonnull MouseHookParent hook) {
        hooks.add(hook);
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    public ImmutableList<MouseHookParent> getHooks() {
        return ImmutableList.copyOf(hooks);
    }
}