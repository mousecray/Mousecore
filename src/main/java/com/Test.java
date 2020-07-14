package com;

import ru.mousecray.mousecore.api.asm.method.MouseConsumer;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public class Get {
        private Map<String, MouseConsumer> methods = new HashMap<>();

        public void femr(Integer i) {
            methods.get("get").onMethodExecuted(i);
        }
    }
}