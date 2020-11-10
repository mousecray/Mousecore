package ru.mousecray.mousecore.core.utils;

public class ByteClassLoader extends ClassLoader {

    public ByteClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}