package ru.mousecray.mousecore.api.asm.method.utils;

import ru.mousecray.mousecore.api.asm.method.MouseValue;

import javax.annotation.Nonnull;
import java.util.Map;

@FunctionalInterface
public interface MouseSpecialExecutor {

    @Nonnull
    MouseValue onExecuted(Map<MouseSpecialField, MouseValue> specialFields, MouseValue... pars);
}
