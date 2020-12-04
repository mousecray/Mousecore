package ru.mousecray.mousecore.api.asm.method;

import org.objectweb.asm.Type;
import ru.mousecray.mousecore.api.asm.MinecraftClass;
import ru.mousecray.mousecore.api.asm.method.utils.MouseReturnCondition;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialExecutor;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialField;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialFieldType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class MouseMethod {
    private final String name;
    private final MinecraftClass returnClass;
    private final MouseReturnCondition condition;
    private final MinecraftClass[] arguments;
    private final MouseSpecialExecutor methodBody;
    private final Map<MouseSpecialField, MouseValue> fields;
    private String descriptor;

    public MouseMethod(String name, MouseReturnCondition condition, MouseSpecialExecutor methodBody, MinecraftClass returnClass, @Nullable Map<MouseSpecialField, MouseValue> fields, MinecraftClass... arguments) {
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
        return fields.get(new MouseSpecialField(type, name));
    }

    public MouseReturnCondition getReturnCondition() {
        return condition;
    }

    public String getNewDescriptor() {
        int length = arguments == null ? 0 : arguments.length;
        Type[] argTypes = new Type[length];
        for (int i = 0; i < length; ++i) argTypes[i] = Type.getType(arguments[i].getDescriptor());
        return descriptor = Type.getMethodDescriptor(Type.getType(returnClass.getDescriptor()), argTypes);
    }

    @Nonnull
    public MouseValue onExecuted(MouseValue... pars) {
        return methodBody.onExecuted(fields, pars);
    }
}