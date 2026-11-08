/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseLambdas;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConditionalListType<T extends CustomType<?>> extends ListType<ConditionalValType<T>, T> {
    private ConditionalListType(Class<T> valClass, @Nullable Function<String, T> valCreator) {
        super(checkValClass(valClass), valCreator);
    }

    private ConditionalListType(
            Class<T> valClass, @Nullable Function<String, T> valCreator, @Nonnull List<ConditionalValType<T>> values
    ) {
        super(checkValClass(valClass), valCreator, values);
    }

    private static <T extends CustomType<?>> Class<T> checkValClass(Class<T> valClass) {
        if (ListType.class.isAssignableFrom(valClass)) {
            throw new UnsupportedValException("Enclosing ListTypes is not support");
        }
        return valClass;
    }

    @SafeVarargs
    public static <T extends CustomType<?>> ConditionalListType<T> createFrom(
            Class<T> valClass,
            @Nullable Function<String, T> valCreator,
            @Nonnull T... values
    ) {
        return new ConditionalListType<>(
                valClass, valCreator,
                MouseCollections.mapAsList(ConditionalValType::create, true, values)
        );
    }

    @SafeVarargs
    public static <T extends CustomType<?>> ConditionalListType<T> createFrom(Class<T> valClass, @Nonnull T... values) {
        return createFrom(valClass, null, values);
    }

    public static <T extends CustomType<?>> ConditionalListType<T> createFrom(
            Class<T> valClass,
            @Nullable Function<String, T> valCreator,
            @Nonnull List<T> values
    ) {
        return new ConditionalListType<>(
                valClass, valCreator,
                MouseCollections.map(ConditionalValType::create, true, values)
        );
    }

    public static <T extends CustomType<?>> ConditionalListType<T> createFrom(Class<T> valClass, @Nonnull List<T> values) {
        return createFrom(valClass, null, values);
    }

    @SafeVarargs
    public static <T extends CustomType<?>> ConditionalListType<T> create(
            Class<T> valClass,
            @Nullable Function<String, T> valCreator,
            @Nonnull ConditionalValType<T>... values
    ) {
        return new ConditionalListType<>(
                valClass, valCreator,
                MouseCollections.mapAsList(MouseLambdas::THIS, true, values)
        );
    }

    @SafeVarargs
    public static <T extends CustomType<?>> ConditionalListType<T> create(Class<T> valClass, @Nonnull ConditionalValType<T>... values) {
        return create(valClass, null, values);
    }

    public static <T extends CustomType<?>> ConditionalListType<T> create(
            Class<T> valClass,
            @Nullable Function<String, T> valCreator,
            @Nonnull List<ConditionalValType<T>> values
    ) {
        return new ConditionalListType<>(
                valClass, valCreator,
                MouseCollections.map(MouseLambdas::THIS, true, values)
        );
    }

    public static <T extends CustomType<?>> ConditionalListType<T> create(Class<T> valClass, @Nonnull List<ConditionalValType<T>> values) {
        return create(valClass, null, values);
    }

    @SuppressWarnings("unchecked") @Override
    protected <TYPE extends ListType<ConditionalValType<T>, T>> TYPE createType(List<ConditionalValType<T>> list) {
        return (TYPE) create(getValClass(), getValCreator(), list);
    }

    public static <T extends CustomType<T>> ConditionalListType<T> copyExcludeValCreator(
            ConditionalListType<T> list, Function<String, T> valCreator
    ) {
        return new ConditionalListType<>(list.getValClass(), valCreator, list.getList());
    }

    @Override
    public boolean containsOriginalValue(@Nullable T value) {
        boolean flag = false;
        for (ConditionalValType<T> val : getList()) {
            T v = val.getValue();
            if (val.isAnti().asBoolean()) flag = !v.equals(value);
            else flag = v == value;
        }
        return flag;
    }

    public static <T extends CustomType<?>> ConditionalListType<T> fromString(
            Class<T> valClass,
            @Nullable String rawString,
            @Nullable Function<String, T> valCreator,
            @Nullable MouseLogger logger
    ) {
        if (rawString == null) return createFrom(valClass, valCreator);

        ConditionalListType<T> list  = createFrom(valClass, valCreator);
        String[]               split = rawString.split(",");
        for (String s : split) {
            String str = MouseStrings.trimWith(s, true, '\t');
            if (!StringUtils.isEmpty(str)) {
                if (!str.contains("\t")) {
                    try {
                        ConditionalValType<T> val;
                        if (valCreator != null) val = ConditionalValType.create(valCreator.apply(str));
                        else val = ConditionalValType.fromString(valClass, str);

                        list.addValue(val);
                    } catch (NumberFormatException e) {
                        if (logger != null) {
                            logger.atWarn()
                                    .withPrefix("Customtype")
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("ConditionalListType skipped broken or unsupported value '{0}'", str);
                        }
                    }
                }
            }
        }

        return list;
    }
}