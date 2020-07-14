package ru.mousecray.testmod.core;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassFieldSource<Type> {
    private final Class sourceClass;

    public ClassFieldSource(Class sourceClass) {
        this.sourceClass = sourceClass;
    }

    public List<Type> getAllFields() {
        return Arrays.stream(sourceClass.getFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()))
                .flatMap(field -> {
                    try {
                        @SuppressWarnings("unchecked") Type elem = (Type) field.get(null);
                        return Stream.of(elem);
                    } catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
                        e.printStackTrace();
                        return Stream.empty();
                    }
                }).collect(Collectors.toList());
    }
}