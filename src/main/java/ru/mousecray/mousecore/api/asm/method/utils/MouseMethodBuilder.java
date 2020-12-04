package ru.mousecray.mousecore.api.asm.method.utils;

import com.google.common.collect.ImmutableMap;
import org.objectweb.asm.Opcodes;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.method.MouseExecutor;
import ru.mousecray.mousecore.api.asm.method.MouseMethod;
import ru.mousecray.mousecore.api.asm.method.MouseValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class MouseMethodBuilder implements Opcodes {
    private final Map<MouseSpecialField, MouseValue> fields = new HashMap<>();
    private String name;
    private int modifiers = ACC_PUBLIC;
    private MinecraftClass returnClass = MinecraftClass.Void;
    private MouseReturnCondition condition = MouseReturnCondition.NEVER;
    private MinecraftClass[] arguments;
    private MouseSpecialExecutor methodBody = (MouseExecutor) e -> new MouseValue();
    private MouseMethod generatedMethod;

    public static MouseMethodBuilder create(String name) {
        return new MouseMethodBuilder().setName(name);
    }

    public MouseMethodBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public synchronized MouseMethodBuilder defineSpecialField(String name, MouseSpecialFieldType type) {
        fields.put(new MouseSpecialField(type, name), new MouseValue());
        return this;
    }

    public MouseMethodBuilder setCondition(MouseReturnCondition condition) {
        this.condition = condition;
        return this;
    }

    public MouseMethodBuilder setVoid() {
        condition = MouseReturnCondition.NEVER;
        returnClass = MinecraftClass.Void;
        return this;
    }

    public MouseMethodBuilder setPublic() {

        return this;
    }

    public MouseMethodBuilder setArguments(MinecraftClass... arguments) {
        this.arguments = arguments;
        return this;
    }

    public MouseMethodBuilder setReturnClass(MinecraftClass returnClass) {
        this.returnClass = returnClass;
        return this;
    }

    public MouseMethodBuilder setMethodBody(MouseExecutor methodBody) {
        this.methodBody = methodBody;
        return this;
    }

    public MouseMethodBuilder setMethodBody(MouseSpecialExecutor methodBody) {
        this.methodBody = methodBody;
        return this;
    }

    @Nonnull
    public MouseMethod build() {
        return new MouseMethod(name, condition,
                methodBody == null ? (MouseExecutor) values -> new MouseValue() : methodBody,
                returnClass,
                fields.isEmpty() ? null : ImmutableMap.copyOf(fields),
                arguments);
    }
}