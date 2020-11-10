package ru.mousecray.mousecore.api.asm.method;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.asm.method.utils.MouseExecutor;
import ru.mousecray.mousecore.api.asm.method.utils.MouseReturnCondition;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialFieldType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class MouseMethodBuilder {
    private String name;
    private Class returnClass;
    private MouseReturnCondition condition;
    private Class<?>[] arguments;
    private MouseExecutor methodBody;
    private Map<Pair<String, MouseSpecialFieldType>, MouseValue> fields = new HashMap<>();
    private MouseMethod generatedMethod;

    public static MouseMethodBuilder create(String name) {
        return new MouseMethodBuilder().setName(name);
    }

    public MouseMethodBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MouseMethodBuilder defineSpecialField(MouseSpecialFieldType type, String name) {
        fields.put(Pair.of(name, type), null);
        return this;
    }

    public MouseMethodBuilder setCondition(MouseReturnCondition condition) {
        this.condition = condition;
        return this;
    }

    public MouseMethodBuilder setVoid() {
        condition = MouseReturnCondition.NEVER;
        returnClass = void.class;
        return this;
    }

    public MouseMethodBuilder setArguments(Class<?>... arguments) {
        this.arguments = arguments;
        return this;
    }

    public MouseMethodBuilder setReturnClass(Class returnClass) {
        this.returnClass = returnClass;
        return this;
    }

    public MouseMethodBuilder setMethodBody(MouseExecutor methodBody) {
        this.methodBody = methodBody;
        return this;
    }

    public MouseMethodBuilder setMethodBody(Consumer<MouseMethod> methodBody) {
        MouseMethod method = new MouseMethod(name, condition, values -> new MouseValue(), returnClass, fields, arguments);
        return this;
    }

    @Nonnull
    public MouseMethod build() {
        return new MouseMethod(name, condition,
                methodBody == null ? values -> new MouseValue() : methodBody,
                returnClass,
                fields.isEmpty() ? null : ImmutableMap.copyOf(fields),
                arguments);
    }
}