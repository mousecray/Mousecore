package ru.mousecray.mousecore.api.asm;

public class MinecraftClass {

    public static final MinecraftClass
            Minecraft = new MinecraftClass("Lnet/minecraft/client/Minecraft;"),
            BlockAir = new MinecraftClass("Lnet/minecraft/block/BlockAir;"),
            BlockAnvil = new MinecraftClass("Lnet/minecraft/block/BlockAnvil;"),
            Void = new MinecraftClass("V"),
            Int = new MinecraftClass("I"),
            Boolean = new MinecraftClass("Z"),
            Double = new MinecraftClass("D"),
            Float = new MinecraftClass("F"),
            Byte = new MinecraftClass("B"),
            Short = new MinecraftClass("S"),
            Long = new MinecraftClass("J"),
            Char = new MinecraftClass("C"),
            String = new MinecraftClass("Ljava/lang/String;"),
            IntegerOBJ = new MinecraftClass("Ljava/lang/Integer;"),
            BooleanOBJ = new MinecraftClass("Ljava/lang/Boolean;"),
            DoubleOBJ = new MinecraftClass("Ljava/lang/Double;"),
            FloatOBJ = new MinecraftClass("Ljava/lang/Float;"),
            ByteOBJ = new MinecraftClass("Ljava/lang/Byte;"),
            ShortOBJ = new MinecraftClass("Ljava/lang/Short;"),
            LongOBJ = new MinecraftClass("Ljava/lang/Long;"),
            CharOBJ = new MinecraftClass("Ljava/lang/Character;");

    private final String descriptor;

    public MinecraftClass(String descriptor) {
        this.descriptor = descriptor;
    }

    public static MinecraftClass fromCanonicalName(String name) {
        return new MinecraftClass("L" + name.replace('.', '/') + ";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return descriptor.equals(((MinecraftClass) o).descriptor);
    }

    public String getCanonicalName() {
        return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
    }

    public String getInternalName() {
        return descriptor.substring(1, descriptor.length() - 1);
    }

    public String getDescriptor() {
        return descriptor;
    }
}