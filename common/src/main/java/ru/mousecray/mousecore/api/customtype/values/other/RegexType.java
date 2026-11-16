/*******************************************************************************
 *  Copyright © 2026 mousecray
 *  Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.other;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.OtherType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.UnsupportedValException;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class RegexType extends OtherType<String> {
    private static final MouseUtils.Equator<RegexType> EQUATOR = MouseUtils.Equator
            .<RegexType>equaling(RegexType::asString)
            .thenEqualing(RegexType::getFlags);

    public static final RegexType MATCH_ALL = new RegexType(".*", 0, Pattern.compile(".*"));

    static {
        storage.put(RegexType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException("Empty regex string");
            return parse(str);
        });
    }

    private final           int     flags;
    private final transient Pattern compiledPattern;

    private RegexType(String pattern, int flags, Pattern compiledPattern) {
        super(pattern);
        this.flags = flags;
        this.compiledPattern = compiledPattern;
    }

    public static RegexType create(String pattern, int flags) {
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        try {
            @SuppressWarnings("MagicConstant") Pattern compiled = Pattern.compile(pattern, flags);
            return new RegexType(pattern, flags, compiled);
        } catch (PatternSyntaxException e) { throw new ValueFormatException("Invalid regex pattern: '" + pattern + "' (" + e.getDescription() + ")", e); }
    }

    public static RegexType create(String pattern) {
        return create(pattern, 0);
    }

    public static RegexType create(Pattern pattern) {
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        return new RegexType(pattern.pattern(), pattern.flags(), pattern);
    }

    public static RegexType parse(@Nullable String raw) {
        String clean = MouseStrings.trimWith(raw, true, '\t');
        if (clean == null || clean.isEmpty()) throw new ValueFormatException("Cannot parse empty regex string");

        if (clean.length() >= 2 && clean.startsWith("/")) {
            int lastSlash = clean.lastIndexOf('/');
            if (lastSlash > 0) {
                String patternPart = clean.substring(1, lastSlash);
                String flagsPart   = clean.substring(lastSlash + 1).toLowerCase();

                int flags = 0;
                for (char c : flagsPart.toCharArray()) {
                    switch (c) {
                        case 'i':
                            flags |= Pattern.CASE_INSENSITIVE;
                            break;
                        case 'm':
                            flags |= Pattern.MULTILINE;
                            break;
                        case 's':
                            flags |= Pattern.DOTALL;
                            break;
                        case 'u':
                            flags |= Pattern.UNICODE_CASE;
                            break;
                        case 'd':
                            flags |= Pattern.UNIX_LINES;
                            break;
                        case 'x':
                            flags |= Pattern.COMMENTS;
                            break;
                        default:
                            throw new ValueFormatException("Unknown regex flag: '" + c + "' in '" + raw + "'");
                    }
                }
                return create(patternPart, flags);
            }
        }

        return create(clean, 0);
    }

    public String asString()                    { return value; }
    public Pattern asPattern()                  { return compiledPattern; }
    public int getFlags()                       { return flags; }

    public Predicate<String> asPredicate()      { return compiledPattern.asPredicate(); }
    public Predicate<String> asMatchPredicate() { return compiledPattern.asMatchPredicate(); }

    public boolean matches(@Nullable String input) {
        return input != null && compiledPattern.matcher(input).matches();
    }

    public boolean find(@Nullable String input) {
        return input != null && compiledPattern.matcher(input).find();
    }

    @Override public IValType getValType() { return ValType.REGEX; }

    @SuppressWarnings("unchecked")
    @Override
    public <TYPE extends OtherType<String>> TYPE createType(Object val) {
        if (val instanceof RegexType) return (TYPE) val;
        else if (val instanceof Pattern) return (TYPE) create((Pattern) val);
        else if (val instanceof String) return (TYPE) parse((String) val);
        throw new UnsupportedValException("Cannot create RegexType from " + val);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object o) { return EQUATOR.test(this, o); }
    @Override public int hashCode() { return Objects.hash(value, flags); }

    public int compareTo(RegexType o) {
        return Comparator
                .<RegexType, String>comparing(RegexType::asString)
                .thenComparingInt(RegexType::getFlags)
                .compare(this, o);
    }

    @Override
    public String toString() {
        if (flags == 0) return "/" + value + "/";
        StringBuilder sb = new StringBuilder("/").append(value).append('/');
        if ((flags & Pattern.CASE_INSENSITIVE) != 0) sb.append('i');
        if ((flags & Pattern.MULTILINE) != 0) sb.append('m');
        if ((flags & Pattern.DOTALL) != 0) sb.append('s');
        if ((flags & Pattern.UNICODE_CASE) != 0) sb.append('u');
        if ((flags & Pattern.UNIX_LINES) != 0) sb.append('d');
        if ((flags & Pattern.COMMENTS) != 0) sb.append('x');
        return sb.toString();
    }
}