package com;

public class Test {
    public static void main(String[] args) {
        int x = 13;
        new Get(x);
    }

    public static class Get {

        public Get(Object obj) {
            System.out.println(obj.getClass());
        }
    }
}