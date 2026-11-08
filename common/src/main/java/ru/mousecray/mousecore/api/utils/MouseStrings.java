/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import org.apache.commons.lang3.StringUtils;
import ru.mousecray.mousecore.api.anno.Fast;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.anno.Slowly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class MouseStrings {
    @Nullable @Fast
    public static String trimWith(@Nullable String str, boolean trimSpace, char target) {
        if (str == null) return null;

        int    len = str.length(), st = 0;
        char[] val = str.toCharArray();

        while ((st < len) && ((trimSpace && val[st] <= ' ') || val[st] == target)) ++st;
        while ((st < len) && ((trimSpace && val[len - 1] <= ' ') || val[len - 1] == target)) len--;
        return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
    }

    @Nullable @Slowly
    public static String trimWith(@Nullable String str, boolean trimSpace, char... targets) {
        if (str == null) return null;

        int    len = str.length(), st = 0;
        char[] val = str.toCharArray();

        while ((st < len) && ((trimSpace && val[st] <= ' ') || MouseCollections.hasAny(val[st], targets))) ++st;
        while ((st < len) && ((trimSpace && val[len - 1] <= ' ') || MouseCollections.hasAny(val[len - 1], targets))) len--;
        return ((st > 0) || (len < val.length)) ? str.substring(st, len) : str;
    }

    @Nullable
    public static String subAndTrimWithTabs(@Nullable String str, @Nullable String rangeStart, @Nullable String rangeEnd) {
        if (str == null) return null;

        String result = str;
        if (rangeStart != null && result.contains(rangeStart)) {
            int last            = result.lastIndexOf(rangeStart);
            int lengthThreshold = last + rangeStart.length();
            if (lengthThreshold <= result.length()) result = result.substring(lengthThreshold);
            else result = "";
        }
        if (rangeEnd != null && result.contains(rangeEnd)) {
            int first = result.indexOf(rangeEnd);
            result = result.substring(0, first);
        }
        return trimWith(result, true, '\t');
    }

    public static List<String> splitWordsBaseOnLength(String input, @Nullable String delimiter, int maxLength) {
        if (StringUtils.isEmpty(input)) return Collections.emptyList();
        if (delimiter == null) delimiter = " ";

        String[]          words = input.split(" ");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("");
        for (String word : words) {
            int    lineIndex = lines.size() - 1;
            String line      = lines.get(lineIndex);
            if (line.isEmpty()) lines.set(lineIndex, word);
            else if (word.length() + 1 <= (maxLength - line.length())) lines.set(lineIndex, line + " " + word);
            else lines.add(delimiter + word);
        }
        return lines;
    }

    public static int compare(@Nullable String val1, @Nullable String val2) {
        return val1 == null ? val2 == null ? 0 : -1 : val2 == null ? 1 : val1.compareTo(val2);
    }

    /**
     * @param text String template like "{0}, {1}, {2}"
     * @param args Objects that be placed to template
     * @return String like "object1, object2, object3"
     */
    @Fast
    @Nullable
    public static String format(@Nullable String text, @Nullable Object... args) {
        if (text == null) return null;
        if (args == null || args.length == 0) return text;

        int           estimatedSize = text.length() + args.length * 16;
        StringBuilder sb            = new StringBuilder(estimatedSize);

        int len     = text.length();
        int lastPos = 0;

        while (lastPos < len) {
            int openBrace = text.indexOf('{', lastPos);
            if (openBrace == -1) {
                sb.append(text, lastPos, len);
                break;
            }

            sb.append(text, lastPos, openBrace);

            int closeBrace = text.indexOf('}', openBrace + 1);
            if (closeBrace == -1) {
                sb.append(text, openBrace, len);
                break;
            }

            int     index         = 0;
            boolean isValidNumber = openBrace + 1 < closeBrace;
            for (int i = openBrace + 1; i < closeBrace; i++) {
                char c = text.charAt(i);
                if (c >= '0' && c <= '9') index = index * 10 + (c - '0');
                else {
                    isValidNumber = false;
                    break;
                }
            }

            if (isValidNumber) {
                if (index >= 0 && index < args.length) sb.append(args[index]);
                else sb.append(text, openBrace, closeBrace + 1);
            } else sb.append(text, openBrace, closeBrace + 1);

            lastPos = closeBrace + 1;
        }

        return sb.toString();
    }
}