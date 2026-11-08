/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public final class MouseJson {
    public static long getLong(JsonObject json, String memberName) {
        if (json.has(memberName)) return getLong(json.get(memberName), memberName);
        else throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
    }

    public static long getLong(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) return json.getAsLong();
        else throw new JsonSyntaxException("Expected " + memberName + " to be a Long, was " + toString(json));
    }

    public static String toString(JsonElement json) {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(json), "...", 10);

        if (json == null) return "null (missing)";
        else if (json.isJsonNull()) return "null (json)";
        else if (json.isJsonArray()) return "an array (" + s + ")";
        else if (json.isJsonObject()) return "an object (" + s + ")";
        else {
            if (json.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();

                if (jsonprimitive.isNumber()) return "a number (" + s + ")";
                if (jsonprimitive.isBoolean()) return "a boolean (" + s + ")";
            }

            return s;
        }
    }

    private MouseJson() { throw new UnsupportedOperationException("Cannot create utility class"); }
}