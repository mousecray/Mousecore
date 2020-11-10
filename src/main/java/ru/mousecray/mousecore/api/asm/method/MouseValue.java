package ru.mousecray.mousecore.api.asm.method;

import javax.annotation.Nonnull;

public class MouseValue {
    protected MouseValueType type;
    protected Object value;
    protected boolean isPrimitive;

    public MouseValue() {
        type = MouseValueType.VOID;
    }

    public MouseValue(@Nonnull Object value) {
        setValue(value);
    }

    public MouseValue(int value) {
        setValue(value);
    }

    public MouseValue(double value) {
        setValue(value);
    }

    public MouseValue(float value) {
        setValue(value);
    }

    public MouseValue(byte value) {
        setValue(value);
    }

    public MouseValue(char value) {
        setValue(value);
    }

    public MouseValue(short value) {
        setValue(value);
    }

    public MouseValue(long value) {
        setValue(value);
    }

    public MouseValue(boolean value) {
        setValue(value);
    }

    public boolean isObject() {
        return !isPrimitive;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public MouseValueType getValueType() {
        return type;
    }

    public int getInteger() {
        return (int) value;
    }

    public double getDouble() {
        return (double) value;
    }

    public float getFloat() {
        return (float) value;
    }

    public byte getByte() {
        return (byte) value;
    }

    public char getChar() {
        return (char) value;
    }

    public short getShort() {
        return (short) value;
    }

    public long getLong() {
        return (long) value;
    }

    public boolean getBoolean() {
        return (boolean) value;
    }

    public String getString() {
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    public void setValue(Object value) {
        type = MouseValueType.OBJECT;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
        type = MouseValueType.INT;
        isPrimitive = true;
    }

    public void setValue(float value) {
        type = MouseValueType.FLOAT;
        isPrimitive = true;
        this.value = value;
    }

    public void setValue(double value) {
        type = MouseValueType.DOUBLE;
        isPrimitive = true;
        this.value = value;
    }

    public void setValue(long value) {
        type = MouseValueType.LONG;
        isPrimitive = true;
        this.value = value;
    }

    public void setValue(short value) {
        type = MouseValueType.SHORT;
        isPrimitive = true;
        this.value = value;
    }

    public void setValue(char value) {
        type = MouseValueType.CHAR;
        isPrimitive = true;
        this.value = value;
    }

    public void setValue(boolean value) {
        type = MouseValueType.BOOLEAN;
        isPrimitive = true;
        this.value = value;
    }

    public void setValue(byte value) {
        type = MouseValueType.BYTE;
        isPrimitive = true;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <T> MouseTypedValue<T> getTypedValue() {
        return new MouseTypedValue<>((T) value);
    }

    public static class MouseTypedValue<T> extends MouseValue {

        public MouseTypedValue(T value) {
            super(value);
        }

        @Override
        public int getInteger() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble() {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat() {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte() {
            throw new UnsupportedOperationException();
        }

        @Override
        public char getChar() {
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getBoolean() {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public T getValue() {
            return (T) value;
        }
    }
}