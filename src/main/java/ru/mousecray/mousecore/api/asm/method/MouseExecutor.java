package ru.mousecray.mousecore.api.asm.method;


import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialExecutor;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialField;

import javax.annotation.Nonnull;
import java.util.Map;

@FunctionalInterface
public interface MouseExecutor extends MouseSpecialExecutor {

    @Nonnull
    @Override
    default MouseValue onExecuted(Map<MouseSpecialField, MouseValue> specialFields, MouseValue... pars) {
        return onExecuted(pars);
    }

    @Nonnull
    MouseValue onExecuted(MouseValue... pars);
}