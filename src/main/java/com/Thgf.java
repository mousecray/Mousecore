package com;

import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.asm.method.utils.MouseSpecialFieldType;

import java.util.HashMap;
import java.util.Map;

public class Thgf {
    public static void main(String[] args) {
        MouseSpecialFieldType type1 = MouseSpecialFieldType.GLOBAL;
        String s1 = "test";
        Map<Pair<String, MouseSpecialFieldType>, Integer> map = new HashMap<>();
        map.put(Pair.of(s1, type1), 5);

        MouseSpecialFieldType type2 = MouseSpecialFieldType.GLOBAL;
        String s2 = "test2";
        System.out.println(map.get(Pair.of(s2, type2)));
    }
}