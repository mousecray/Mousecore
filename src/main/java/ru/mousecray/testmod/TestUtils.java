package ru.mousecray.testmod;

import ru.mousecray.testmod.core.INameProvider;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class TestUtils {
    public static String getName(Object o) {
        if (o instanceof INameProvider)
            return ((INameProvider) o).getName();
        else
            return getName(o.getClass());
    }

    @Nonnull
    public static String getName(@Nonnull Class clazz) {
        String r = clazz.getSimpleName().chars().flatMap(i -> {
            if (Character.isUpperCase(i)) {
                return IntStream.of('_', Character.toLowerCase(i));
            }
            else
                return IntStream.of(i);
        }).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        if (r.startsWith("_"))
            return r.substring(1);
        else
            return r;
    }
}