package ru.mousecray.mousecore.api.asm.event;

import com.google.common.collect.ImmutableList;
import ru.mousecray.mousecore.api.asm.MouseContainer;
import ru.mousecray.mousecore.api.asm.transformer.MouseHookParent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MouseLoadEvent {
    private final List<MouseHookParent> hooks = new ArrayList<>();
    private final String hookid;

    public MouseLoadEvent(String hookid) {
        this.hookid = hookid;
    }

    public void registerHook(@Nonnull MouseHookParent hook) {
        if (hook.getHookid().equals(hookid)) hooks.add(hook);
        else throw new IllegalArgumentException("Attempt to added hook with incompatible id. " +
                "Hook must have id, who same with " + MouseContainer.class.getSimpleName() +
                " id. Container id is " + hookid + "; Hook id is " + hook.getHookid());
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    public ImmutableList<MouseHookParent> getHooks() {
        return ImmutableList.copyOf(hooks);
    }
}