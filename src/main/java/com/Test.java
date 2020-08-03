package com;

import ru.mousecray.mousecore.api.asm.method.MouseConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 99999999; i++) {
            list.add(0);
        }
        long one = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            Integer integer = list.get(i);
        }
        long two = System.currentTimeMillis();
        System.out.println(two - one);
    }

    public class Get {
        private Map<String, MouseConsumer> methods = new HashMap<>();

        public void femr(Integer i) {
            methods.get("get").onMethodExecuted(i);
        }
    }
}