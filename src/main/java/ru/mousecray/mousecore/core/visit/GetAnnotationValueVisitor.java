package ru.mousecray.mousecore.core.visit;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class GetAnnotationValueVisitor extends AnnotationVisitor {

    private final Consumer<String> hookid;

    public GetAnnotationValueVisitor(Consumer<String> hookid) {
        super(Opcodes.ASM5);
        this.hookid = hookid;
    }

    @Override
    public void visit(String name, Object value) {
        if (name.equals("hookid")) hookid.accept((String) value);
        super.visit(name, value);
    }
}
