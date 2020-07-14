package ru.mousecray.mousecore.core.visit;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Consumer;

public class GetAnnotationValueVisitor extends AnnotationVisitor {

    private final Consumer<String> modid;

    public GetAnnotationValueVisitor(Consumer<String> modid) {
        super(Opcodes.ASM5);
        this.modid = modid;
    }

    @Override
    public void visit(String name, Object value) {
        if (name.equals("modid")) modid.accept((String) value);
        super.visit(name, value);
    }
}
