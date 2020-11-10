package ru.mousecray.mousecore.api.asm.method;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.method.utils.MouseExecutor;
import ru.mousecray.mousecore.api.asm.method.utils.MouseReturnCondition;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialFieldType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class MouseMethod implements MouseExecutor {
    private final String name;
    private final Class returnClass;
    private final MouseReturnCondition condition;
    private final Class<?>[] arguments;
    private final MouseExecutor methodBody;
    private final Map<Pair<String, MouseSpecialFieldType>, MouseValue> fields;
    private String descriptor;

    public MouseMethod(String name, MouseReturnCondition condition, MouseExecutor methodBody, Class returnClass, @Nullable Map<Pair<String, MouseSpecialFieldType>, MouseValue> fields, Class<?>... arguments) {
        this.name = name;
        this.returnClass = returnClass;
        this.condition = condition;
        this.fields = fields;
        this.arguments = arguments;
        this.methodBody = methodBody;
        getNewDescriptor();
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public MouseValue getSpecialField(MouseSpecialFieldType type, String name) {
        return fields.get(Pair.of(name, type));
    }

    public MouseReturnCondition getReturnCondition() {
        return condition;
    }

    public String getNewDescriptor() {
        int length = arguments == null ? 0 : arguments.length;
        Type[] argTypes = new Type[length];
        for (int i = 0; i < length; ++i) argTypes[i] = Type.getType(arguments[i]);
        return descriptor = Type.getMethodDescriptor(Type.getType(returnClass), argTypes);
    }

    @Nonnull
    @Override
    public MouseValue onExecuted(MouseValue... pars) {
        return methodBody.onExecuted(pars);
    }
}