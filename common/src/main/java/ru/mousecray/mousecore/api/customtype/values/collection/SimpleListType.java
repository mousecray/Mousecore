/*******************************************************************************
 *  Copyright © 2026 mousecray
 *  Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.collection;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.ListType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.logic.ConditionalValType;
import ru.mousecray.mousecore.api.customtype.values.logic.PlusMinusType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class SimpleListType<T extends CustomType<?>> extends ListType<ConditionalValType<T>, T> {
    private static final MouseUtils.Equator<SimpleListType<?>> EQUATOR = MouseUtils.Equator
            .<SimpleListType<?>>equaling(SimpleListType::getListComponentValType)
            .thenEqualing(SimpleListType::isConditional)
            .thenEqualing(SimpleListType::isUnique)
            .thenEqualing(SimpleListType::getList);

    private final boolean isConditional;
    private final boolean isUnique;

    private SimpleListType(IValType listComponentType, @Nullable Function<String, T> valCreator, boolean isConditional, boolean isUnique) {
        this(listComponentType, valCreator, isConditional, isUnique, Collections.emptyList());
    }

    private SimpleListType(
            IValType listComponentType, @Nullable Function<String, T> valCreator, boolean isConditional, boolean isUnique,
            List<ConditionalValType<T>> values
    ) {
        super(checkValType(listComponentType), valCreator, normalizeValues(values, isConditional, isUnique));
        this.isConditional = isConditional;
        this.isUnique = isUnique;
    }

    private static IValType checkValType(IValType listComponentType) {
        if (listComponentType == ValType.UNDEFINED) throw new UnsupportedValException("SimpleListTypes with undefined value is not supported");
        if (!listComponentType.getCategory().canBeContained()) {
            throw new UnsupportedValException("Containers cannot be stored inside SimpleListType: " + listComponentType.getDisplayName());
        }
        return listComponentType;
    }

    private static <T extends CustomType<?>> List<ConditionalValType<T>> normalizeValues(
            List<ConditionalValType<T>> values, boolean isConditional, boolean isUnique
    ) {
        Objects.requireNonNull(values);
        if (values.isEmpty()) return Collections.emptyList();

        List<ConditionalValType<T>> result = new ArrayList<>(values.size());
        for (ConditionalValType<T> val : values) {
            if (val == null) continue;
            ConditionalValType<T> entry = (!isConditional && val.isAnti().asBoolean())
                    ? ConditionalValType.create(val.getValue(), PlusMinusType.FALSE)
                    : val;
            if (isUnique && result.contains(entry)) continue;
            result.add(entry);
        }
        return result;
    }

    @Override public IValType getValType() { return ValType.SIMPLE_LIST; }
    public boolean isConditional()         { return isConditional; }
    public boolean isUnique()              { return isUnique; }

    @SuppressWarnings("unchecked") @Override
    protected <TYPE extends ListType<ConditionalValType<T>, T>> TYPE createType(List<ConditionalValType<T>> list) {
        return (TYPE) new SimpleListType<>(getListComponentValType(), getValCreator(), isConditional, isUnique, list);
    }

    public SimpleListType<T> withAddedOriginal(T value) {
        return withAdded(ConditionalValType.create(value, PlusMinusType.FALSE));
    }

    public SimpleListType<T> withRemovedOriginal(T value) {
        return withRemoved(ConditionalValType.create(value, PlusMinusType.FALSE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <TYPE extends ListType<ConditionalValType<T>, T>> TYPE withAdded(ConditionalValType<T> value) {
        Objects.requireNonNull(value);
        ConditionalValType<T> normalized = (!isConditional && value.isAnti().asBoolean())
                ? ConditionalValType.create(value.getValue(), PlusMinusType.FALSE)
                : value;
        if (isUnique && containsValue(normalized)) return (TYPE) this;
        return super.withAdded(normalized);
    }

    @Override
    public boolean containsOriginalValue(@Nullable T value) {
        if (value == null || isEmpty()) return false;

        boolean hasPositive     = false;
        boolean matchedPositive = false;

        for (ConditionalValType<T> entry : getList()) {
            T v = entry.getValue();

            if (isConditional && entry.isAnti().asBoolean()) {
                if (v.equals(value)) return false;
            } else {
                hasPositive = true;
                if (v.equals(value)) matchedPositive = true;
            }
        }

        return !hasPositive || matchedPositive;
    }

    @SafeVarargs
    public static <T extends CustomType<?>> SimpleListType<T> create(
            IValType listComponentType, boolean isConditional, boolean isUnique,
            @Nullable Function<String, T> valCreator, T... values
    ) {
        return new SimpleListType<>(listComponentType, valCreator, isConditional, isUnique,
                MouseCollections.mapAsList(ConditionalValType::create, true, values));
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(
            IValType listComponentType, boolean isConditional, boolean isUnique,
            @Nullable Function<String, T> valCreator, List<T> values
    ) {
        return new SimpleListType<>(listComponentType, valCreator, isConditional, isUnique,
                MouseCollections.map(ConditionalValType::create, true, values));
    }

    public static <T extends CustomType<?>> SimpleListType<T> create(
            IValType listComponentType, boolean isConditional, boolean isUnique, List<T> values
    ) {
        return create(listComponentType, isConditional, isUnique, null, values);
    }

    public static <T extends CustomType<?>> SimpleListType<T> createWithEntries(
            IValType listComponentType, boolean isConditional, boolean isUnique,
            @Nullable Function<String, T> valCreator, List<ConditionalValType<T>> values
    ) {
        return new SimpleListType<>(listComponentType, valCreator, isConditional, isUnique, values);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomType<?>> SimpleListType<T> fromString(
            IValType listComponentType, boolean isConditional, boolean isUnique,
            @Nullable String rawString, @Nullable Function<String, T> valCreator, @Nullable MouseLogger logger
    ) {
        checkValType(listComponentType);
        if (rawString == null) return new SimpleListType<>(listComponentType, valCreator, isConditional, isUnique);

        List<String>                split   = MouseStrings.splitRespectingBrackets(rawString, ',');
        List<ConditionalValType<T>> entries = new ArrayList<>(split.size());

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

                if (innerVal != null) {
                    ConditionalValType<T> entry = ConditionalValType.create(innerVal, PlusMinusType.create(anti));
                    if (isUnique && entries.contains(entry)) {
                        if (logger != null) {
                            logger.atWarn()
                                    .withPrefix(LogPrefixes.CUSTOMTYPE)
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("Duplicate element '{0}' ignored in unique list", str);
                        }
                    } else {
                        entries.add(entry);
                    }
                } else if (logger != null) {
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

        return new SimpleListType<>(listComponentType, valCreator, isConditional, isUnique, entries);
    }

    @Override @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) { return EQUATOR.equals(getClass(), this, obj); }
    @Override public int hashCode() { return Objects.hash(super.hashCode(), isConditional, isUnique); }
}