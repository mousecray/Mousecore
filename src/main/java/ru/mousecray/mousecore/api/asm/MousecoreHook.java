package ru.mousecray.mousecore.api.asm;

import ru.mousecray.mousecore.api.asm.transformers.AbstractMousecoreTransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicate transformers class with this annotation</p>
 * Warning: Class must instantiate {@link AbstractMousecoreTransformer AbstractMousecoreTransformer}
 * or his children
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MousecoreHook {
    String[] value() default {};
}