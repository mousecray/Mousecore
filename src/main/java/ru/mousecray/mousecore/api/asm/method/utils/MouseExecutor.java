package ru.mousecray.mousecore.api.asm.method.utils;

import ru.mousecray.mousecore.api.asm.method.MouseValue;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface MouseExecutor {
    @Nonnull
    MouseValue onExecuted(MouseValue... pars);
}