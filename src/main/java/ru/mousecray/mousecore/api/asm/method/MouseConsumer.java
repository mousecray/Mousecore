package ru.mousecray.mousecore.api.asm.method;

@FunctionalInterface
public interface MouseConsumer {
    Object onMethodExecuted(Object... pars);
}