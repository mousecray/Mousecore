package ru.mousecray.mousecore.api.asm.method.utils;

import org.apache.commons.lang3.tuple.Pair;

public class MouseSpecialField extends Pair<MouseSpecialFieldType, String> {
    private final MouseSpecialFieldType type;
    private final String name;

    public MouseSpecialField(MouseSpecialFieldType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MouseSpecialFieldType getType() {
        return type;
    }

    @Override
    public String setValue(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MouseSpecialFieldType getLeft() {
        return type;
    }

    @Override
    public String getRight() {
        return name;
    }
}
