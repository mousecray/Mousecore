package com;

public interface Test {
    default int get(String s) {
        return 0;
    }

    void set(int n);
}