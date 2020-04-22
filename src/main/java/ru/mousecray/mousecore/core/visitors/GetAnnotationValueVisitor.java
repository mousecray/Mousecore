package ru.mousecray.mousecore.core.visitors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.function.Supplier;

public class GetAnnotationValueVisitor extends AnnotationVisitor {

    private final Supplier<List<String>> supplier;

    public GetAnnotationValueVisitor(Supplier<List<String>> supplier) {
        super(Opcodes.ASM5);
        this.supplier = supplier;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new ArrayInAnnotationVisitor();
    }

    private class ArrayInAnnotationVisitor extends AnnotationVisitor {

        public ArrayInAnnotationVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(String name, Object value) {
            supplier.get().add((String) value);
            super.visit(name, value);
        }
    }
}