package ru.mousecray.mousecore.api.anno;

public @interface API {
    String owner() default "";
    String provides() default "";
    String apiVersion() default "";
}
