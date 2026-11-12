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

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class ConditionalListType<T extends CustomType<?>> extends ListType<ConditionalValType<T>, T> {
    private ConditionalListType(IValType listComponentType, @Nullable Function<String, T> valCreator) {
        super(checkValType(listComponentType), valCreator);
    }

    private ConditionalListType(IValType listComponentType, @Nullable Function<String, T> valCreator, List<ConditionalValType<T>> values) {
        super(checkValType(listComponentType), valCreator, values);
    }

    private static IValType checkValType(IValType listComponentType) {
        if (listComponentType == ValType.UNDEFINED) throw new UnsupportedValException("ListTypes with undefined value is not support");
        if (listComponentType.getCategory() == ValTypeCategory.LIST) throw new UnsupportedValException("Enclosing ListTypes is not support");
        return listComponentType;
    }

    @SafeVarargs
    public static <T extends CustomType<?>> ConditionalListType<T> create(IValType listComponentType, @Nullable Function<String, T> valCreator, ConditionalValType<T>... values) {
        return new ConditionalListType<>(
                listComponentType, valCreator, MouseCollections.mapAsList(MouseLambdas::THIS, true, values)
        );
    }

    @SafeVarargs
    public static <T extends CustomType<?>> ConditionalListType<T> create(IValType listComponentType, ConditionalValType<T>... values) {
        return create(listComponentType, null, values);
    }

    public static <T extends CustomType<?>> ConditionalListType<T> create(IValType listComponentType, @Nullable Function<String, T> valCreator, List<ConditionalValType<T>> values) {
        return new ConditionalListType<>(
                listComponentType, valCreator, MouseCollections.map(MouseLambdas::THIS, true, values)
        );
    }

    public static <T extends CustomType<?>> ConditionalListType<T> create(IValType listComponentType, List<ConditionalValType<T>> values) {
        return create(listComponentType, null, values);
    }

    @Override public IValType getValType() { return ValType.CONDITIONAL_LIST; }

    @SuppressWarnings("unchecked") @Override
    protected <TYPE extends ListType<ConditionalValType<T>, T>> TYPE createType(List<ConditionalValType<T>> list) {
        return (TYPE) create(getListComponentValType(), getValCreator(), list);
    }

    public static <T extends CustomType<T>> ConditionalListType<T> copyExcludeValCreator(ConditionalListType<T> list, Function<String, T> valCreator) {
        return new ConditionalListType<>(list.getListComponentValType(), valCreator, list.getList());
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

    @SuppressWarnings("unchecked")
    public static <T extends CustomType<?>> ConditionalListType<T> fromString(
            IValType listComponentType,
            @Nullable String rawString,
            @Nullable Function<String, T> valCreator,
            @Nullable MouseLogger logger
    ) {
        if (rawString == null) return create(listComponentType, valCreator);

        ConditionalListType<T> list  = create(listComponentType, valCreator);
        String[]               split = rawString.split(",");
        for (String s : split) {
            String str = MouseStrings.trimWith(s, true, '\t');
            if (!StringUtils.isEmpty(str)) {
                if (!str.contains("\t")) {
                    try {
                        ConditionalValType<T> val;
                        if (valCreator != null) {
                            boolean anti = false;
                            if (str.startsWith("!")) {
                                anti = true;
                                str = MouseStrings.trimWith(str.substring(1), true, '\t');
                            }
                            T innerVal = valCreator.apply(str);
                            val = innerVal != null ? ConditionalValType.create(innerVal, PlusMinusType.create(anti)) : null;
                        } else {
                            //noinspection DataFlowIssue
                            val = (ConditionalValType<T>) ConditionalValType.fromString(listComponentType.getTypeClass(), str);
                        }

                        if (val != null) {
                            list.addValue(val);
                        } else if (logger != null) {
                            logger.atWarn()
                                    .withPrefix(LogPrefixes.CUSTOMTYPE)
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("ConditionalListType skipped null value '{0}'", str);
                        }
                    } catch (NumberFormatException e) {
                        if (logger != null) {
                            logger.atWarn()
                                    .withPrefix(LogPrefixes.CUSTOMTYPE)
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