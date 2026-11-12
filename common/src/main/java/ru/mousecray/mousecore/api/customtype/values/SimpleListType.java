/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.type.ValTypeCategory;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseLambdas;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class SimpleListType<T extends CustomType<?>> extends ListType<T, T> {

    private SimpleListType(IValType listComponentType, @Nullable Function<String, T> valCreator) {
        super(checkValType(listComponentType), valCreator);
    }

    private SimpleListType(IValType listComponentType, @Nullable Function<String, T> valCreator, List<T> values) {
        super(listComponentType, valCreator, values);
    }

    private static IValType checkValType(IValType listComponentType) {
        if (listComponentType == ValType.UNDEFINED) throw new UnsupportedValException("ListTypes with undefined value is not support");
        if (listComponentType.getCategory() == ValTypeCategory.LIST) throw new UnsupportedValException("Enclosing ListTypes is not support");
        return listComponentType;
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, @Nullable Function<String, T> valCreator, T... values) {
        return new SimpleListType<>(
                listComponentType, valCreator, MouseCollections.mapAsList(MouseLambdas::THIS, true, values)
        );
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, @Nullable Function<String, T> valCreator, List<T> values) {
        return new SimpleListType<>(
                listComponentType, valCreator, MouseCollections.map(MouseLambdas::THIS, true, values)
        );
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, List<T> values) {
        return create(listComponentType, null, values);
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, T... values) {
        return create(listComponentType, null, values);
    }

    @Override public IValType getValType() { return ValType.SIMPLE_LIST; }

    @SuppressWarnings("unchecked") @Override
    protected <TYPE extends ListType<T, T>> TYPE createType(List<T> list) {
        return (TYPE) create(getListComponentValType(), getValCreator(), list);
    }

    @Override public boolean containsOriginalValue(@Nullable T value) { return containsValue(value); }

    public static <T extends CustomType<T>> SimpleListType<T> copyExcludeValCreator(
            SimpleListType<T> list, Function<String, T> valCreator
    ) {
        return new SimpleListType<>(list.getListComponentValType(), valCreator, list.getList());
    }


    @SuppressWarnings("unchecked")
    public static <T extends CustomType<?>> SimpleListType<T> fromString(
            IValType listComponentType,
            @Nullable String rawString,
            @Nullable Function<String, T> valCreator,
            @Nullable MouseLogger logger
    ) {
        if (rawString == null) return create(listComponentType, valCreator);

        SimpleListType<T> list  = create(listComponentType, valCreator);
        String[]          split = rawString.split(",");
        for (String s : split) {
            String str = MouseStrings.trimWith(s, true, '\t');
            if (!StringUtils.isEmpty(str)) {
                if (!str.contains("\t")) {
                    try {
                        T val;
                        if (valCreator != null) val = valCreator.apply(str);
                        else {
                            //noinspection DataFlowIssue
                            val = (T) CustomType.parse(listComponentType.getTypeClass(), str);
                        }

                        if (val != null) list.addValue(val);
                        else {
                            if (logger != null) {
                                logger.atWarn()
                                        .withPrefix(LogPrefixes.CUSTOMTYPE)
                                        .withStyle(ConsoleColor.YELLOW_BG)
                                        .log("ListType skipped null value '{0}'", str);
                            }
                        }
                    } catch (NumberFormatException e) {
                        if (logger != null) {
                            logger.atWarn()
                                    .withPrefix(LogPrefixes.CUSTOMTYPE)
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("ListType skipped broken or unsupported value '{0}'", str);
                        }
                    }
                }
            }
        }

        return list;
    }
}