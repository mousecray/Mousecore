/*
 * ***************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 * **************************************************************************
 */

package ru.mousecray.mousecore.core.config.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.mousecray.mousecore.api.config.utils.ConfigLang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ConfigLangLoader {
    private static final Gson GSON = new Gson();

    private ConfigLangLoader() { }

    public static Map<String, Map<ConfigLang, String>> loadRequestedKeys(String modId, Set<String> requestedKeys) {
        Map<String, Map<ConfigLang, String>> result = new HashMap<>();
        if (requestedKeys.isEmpty()) return result;

        for (String key : requestedKeys) {
            result.put(key, new EnumMap<>(ConfigLang.class));
        }

        for (ConfigLang lang : ConfigLang.values()) {
            String path = "/assets/" + modId + "/lang/" + lang.getMinecraftCode() + ".json";
            try (InputStream is = ConfigLangLoader.class.getResourceAsStream(path)) {
                if (is != null) {
                    try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        JsonObject json = GSON.fromJson(reader, JsonObject.class);
                        if (json != null) {
                            for (String key : requestedKeys) {
                                JsonElement el = json.get(key);
                                if (el != null && el.isJsonPrimitive()) {
                                    result.get(key).put(lang, el.getAsString());
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignore) { }
        }
        return result;
    }
}