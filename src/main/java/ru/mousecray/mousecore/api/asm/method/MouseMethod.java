package ru.mousecray.mousecore.api.asm.method;

public class MouseMethod implements MouseConsumer {
    private MouseReturnType type;
    private MouseReturnCondition condition;

    public MouseMethod(MouseReturnType type, MouseReturnCondition condition) {
        this.type = type;
        this.condition = condition;
    }

    @Override
    public Object onMethodExecuted(Object... pars) {
        return null;
    }
}