/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values.collection;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.TupleType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.customtype.values.other.StringType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class SimpleTupleType extends TupleType<SimpleTupleType> {
    private final List<CustomType<?>> elements;

    private SimpleTupleType(List<IValType> schema, List<CustomType<?>> elements) {
        super(schema);
        Objects.requireNonNull(elements, "Elements list cannot be null");
        if (elements.size() != schema.size()) {
            throw new ValueFormatException("Tuple elements count (" + elements.size() + ") does not match schema size (" + schema.size() + ")");
        }
        for (int i = 0; i < elements.size(); i++) {
            CustomType<?> elem     = Objects.requireNonNull(elements.get(i), "Tuple element at index " + i + " is null");
            IValType      expected = schema.get(i);
            if (expected != ValType.UNDEFINED && elem.getValType() != expected) {
                throw new ValueFormatException("Tuple element #" + i + " type mismatch: expected " + expected.getDisplayName() + ", got " + elem.getValType().getDisplayName());
            }
        }
        this.elements = List.copyOf(elements);
    }

    public static SimpleTupleType create(List<IValType> schema, List<CustomType<?>> elements) {
        return new SimpleTupleType(schema, elements);
    }

    public static SimpleTupleType create(List<IValType> schema, CustomType<?>... elements) {
        return new SimpleTupleType(schema, Arrays.asList(elements));
    }

    public static SimpleTupleType create(CustomType<?>... elements) {
        Objects.requireNonNull(elements, "Elements array cannot be null");
        List<IValType> inferredSchema = Arrays.stream(elements)
                .map(CustomType::getValType)
                .collect(Collectors.toList());
        return new SimpleTupleType(inferredSchema, Arrays.asList(elements));
    }

    public static SimpleTupleType create(List<? extends CustomType<?>> elements) {
        Objects.requireNonNull(elements, "Elements list cannot be null");
        List<IValType> inferredSchema = elements.stream()
                .map(CustomType::getValType)
                .collect(Collectors.toList());
        return new SimpleTupleType(inferredSchema, new ArrayList<>(elements));
    }

    @Override
    public SimpleTupleType createType(List<CustomType<?>> elements) {
        return new SimpleTupleType(schema, elements);
    }

    public static SimpleTupleType fromString(
            List<IValType> schema,
            @Nullable String raw,
            @Nullable MouseLogger logger
    ) {
        Objects.requireNonNull(schema, "Schema cannot be null");
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty string as Tuple");

        if (MouseStrings.isWrappedBy(clean, '(', ')') || MouseStrings.isWrappedBy(clean, '[', ']')) clean = clean.substring(1, clean.length() - 1);

        char delimiter;
        if (MouseStrings.indexOfRespectingBrackets(clean, "\n") != -1) delimiter = '\n';
        else if (MouseStrings.indexOfRespectingBrackets(clean, ";") != -1) delimiter = ';';
        else delimiter = ',';

        List<String> rawParts = MouseStrings.splitRespectingBrackets(clean, delimiter);
        List<String> parts    = new ArrayList<>(rawParts.size());
        for (String p : rawParts) {
            String trimmed = MouseStrings.trimWith(p, true, '\t');
            if (trimmed != null && !trimmed.isEmpty()) parts.add(trimmed);
        }

        if (parts.size() != schema.size()) {
            String msg = "Tuple elements count (" + parts.size() + ") does not match schema size (" + schema.size() + ") in: '" + raw + "'";
            if (logger != null) {
                logger.atWarn()
                        .withPrefix(LogPrefixes.CUSTOMTYPE)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log(msg);
            }
            throw new ValueFormatException(msg);
        }

        List<CustomType<?>> parsedElements = new ArrayList<>(schema.size());
        for (int i = 0; i < parts.size(); i++) {
            String                      partStr      = parts.get(i);
            IValType                    expectedType = schema.get(i);
            Class<? extends CustomType> targetClass  = expectedType.getTypeClass();

            if (targetClass == null) parsedElements.add(StringType.create(partStr));
            else {
                CustomType<?> parsed = CustomType.parse(targetClass, partStr);
                if (parsed == null) {
                    String msg = "Failed to parse tuple element #" + i + " as " + expectedType.getDisplayName() + ": '" + partStr + "'";
                    if (logger != null) {
                        logger.atWarn()
                                .withPrefix(LogPrefixes.CUSTOMTYPE)
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log(msg);
                    }
                    throw new ValueFormatException(msg);
                }
                parsedElements.add(parsed);
            }
        }

        return new SimpleTupleType(schema, parsedElements);
    }

    public static SimpleTupleType fromString(List<IValType> schema, @Nullable String raw) {
        return fromString(schema, raw, null);
    }

    public static SimpleTupleType fromString(IValType[] schema, @Nullable String raw) {
        return fromString(Arrays.asList(schema), raw, null);
    }

    @Override public CustomType<?> get(int index) { return elements.get(index); }
    @Override public List<CustomType<?>> asList() { return elements; }
    @Override public CustomType<?>[] asArray()    { return elements.toArray(new CustomType<?>[0]); }
    @Override public IValType getValType()        { return ValType.SIMPLE_TUPLE; }
}