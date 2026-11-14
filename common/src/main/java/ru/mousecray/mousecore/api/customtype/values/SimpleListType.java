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
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class SimpleListType<T extends CustomType<?>> extends ListType<ConditionalValType<T>, T> {
    private static final MouseUtils.Equator<SimpleListType<?>> EQUATOR = MouseUtils.Equator
            .<SimpleListType<?>>equaling(SimpleListType::getListComponentValType)
            .thenEqualing(SimpleListType::isConditional)
            .thenEqualing(SimpleListType::getValCreator)
            .thenEqualing(SimpleListType::getList);

    private final boolean isConditional;

    private SimpleListType(IValType listComponentType, @Nullable Function<String, T> valCreator, boolean isConditional) {
        super(checkValType(listComponentType), valCreator);
        this.isConditional = isConditional;
    }

    private SimpleListType(IValType listComponentType, @Nullable Function<String, T> valCreator, boolean isConditional, List<ConditionalValType<T>> values) {
        super(checkValType(listComponentType), valCreator, values);
        this.isConditional = isConditional;
    }

    private static IValType checkValType(IValType listComponentType) {
        if (listComponentType == ValType.UNDEFINED) throw new UnsupportedValException("SimpleListTypes with undefined value is not supported");
        if (listComponentType.getCategory() == ValTypeCategory.LIST) throw new UnsupportedValException("Enclosing SimpleListTypes is not supported");
        return listComponentType;
    }

    @Override public IValType getValType() { return ValType.SIMPLE_LIST; }

    public boolean isConditional()         { return isConditional; }

    @SuppressWarnings("unchecked")
    @Override
    protected <TYPE extends ListType<ConditionalValType<T>, T>> TYPE createType(List<ConditionalValType<T>> list) {
        return (TYPE) new SimpleListType<>(getListComponentValType(), getValCreator(), isConditional, list);
    }

    public boolean addOriginalValue(T value) {
        return super.addValue(ConditionalValType.create(value, PlusMinusType.FALSE));
    }

    @Override
    public boolean addValue(ConditionalValType<T> value) {
        if (!isConditional && value.isAnti().asBoolean()) {
            return super.addValue(ConditionalValType.create(value.getValue(), PlusMinusType.FALSE));
        }
        return super.addValue(value);
    }

    @Override
    public boolean containsOriginalValue(@Nullable T value) {
        if (value == null) return false;

        boolean flag = false;
        for (ConditionalValType<T> val : getList()) {
            T v = val.getValue();
            if (val.isAnti().asBoolean()) flag = !v.equals(value);
            else flag = v.equals(value);
        }
        return flag;
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, boolean isConditional, @Nullable Function<String, T> valCreator, T... values) {
        return new SimpleListType<>(listComponentType, valCreator, isConditional, MouseCollections.mapAsList(ConditionalValType::create, true, values));
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, boolean isConditional, T... values) {
        return create(listComponentType, isConditional, null, values);
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, boolean isConditional, @Nullable Function<String, T> valCreator, List<T> values) {
        return new SimpleListType<>(listComponentType, valCreator, isConditional, MouseCollections.map(ConditionalValType::create, true, values));
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(IValType listComponentType, boolean isConditional, List<T> values) {
        return create(listComponentType, isConditional, null, values);
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> createWithEntries(IValType listComponentType, boolean isConditional, @Nullable Function<String, T> valCreator, ConditionalValType<T>... values) {
        return new SimpleListType<>(listComponentType, valCreator, isConditional, MouseCollections.mapAsList(MouseLambdas::THIS, true, values));
    }

    public static <T extends CustomType<T>> SimpleListType<T> copyExcludeValCreator(SimpleListType<T> list, Function<String, T> valCreator) {
        return new SimpleListType<>(list.getListComponentValType(), valCreator, list.isConditional(), list.getList());
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomType<?>> SimpleListType<T> fromString(
            IValType listComponentType,
            boolean isConditional,
            @Nullable String rawString,
            @Nullable Function<String, T> valCreator,
            @Nullable MouseLogger logger
    ) {
        SimpleListType<T> list = new SimpleListType<>(listComponentType, valCreator, isConditional);
        if (rawString == null) return list;

        String[] split = rawString.split(",");
        for (String s : split) {
            String str = MouseStrings.trimWith(s, true, '\t');
            if (StringUtils.isEmpty(str) || str.contains("\t")) continue;

            try {
                boolean anti = false;

                if (str.startsWith("!")) {
                    str = MouseStrings.trimWith(str.substring(1), true, '\t');

                    if (isConditional) anti = true;
                    else if (logger != null) {
                        logger.atWarn()
                                .withPrefix(LogPrefixes.CUSTOMTYPE)
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("List does not support negation. The '!' prefix will be ignored for value '{0}'", str);
                    }
                }

                T innerVal;
                if (valCreator != null) innerVal = valCreator.apply(str);
                else {
                    //noinspection DataFlowIssue
                    innerVal = (T) CustomType.parse(listComponentType.getTypeClass(), str);
                }

                if (innerVal != null) list.addValue(ConditionalValType.create(innerVal, PlusMinusType.create(anti)));
                else if (logger != null) {
                    logger.atWarn()
                            .withPrefix(LogPrefixes.CUSTOMTYPE)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ListType skipped null value '{0}'", str);
                }
            } catch (Exception e) {
                if (logger != null) {
                    logger.atWarn()
                            .withPrefix(LogPrefixes.CUSTOMTYPE)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ListType skipped broken value '{0}'", str);
                }
            }
        }

        return list;
    }

    @Override @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) { return EQUATOR.equals(getClass(), this, obj); }
    @Override public int hashCode() { return Objects.hash(super.hashCode(), isConditional); }
}