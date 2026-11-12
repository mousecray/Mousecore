package ru.mousecray.mousecore.core.config.build.val;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.core.config.build.par.AbstractConfigChildBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigValueBuilder<VAL extends ConfigVal<?>, BUILDER extends AbstractConfigChildBuilder> {
    public BUILDER       parBuilder;
    public Consumer<VAL> onComplete;

    protected abstract VAL createValue();

    public final BUILDER buildValue() {
        VAL val = createValue();
        Objects.requireNonNull(val);
        if (onComplete != null) onComplete.accept(val);
        return parBuilder;
    }
}