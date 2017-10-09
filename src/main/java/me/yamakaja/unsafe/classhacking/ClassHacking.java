package me.yamakaja.unsafe.classhacking;

import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Yamakaja on 9/6/17.
 */
public class ClassHacking {

    public static final int MODIFIER_OFFSET = 152;
    public static final int ACCESS_FLAG_OFFSET = 156;

    public static void main(String[] args) throws Exception {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);

        System.load(new File("classhacking").getAbsolutePath());

        System.out.println(Modifier.toString(ClassHacking.getClassModifiers("")));

        long klassPointer = unsafe.arrayIndexScale(Object[].class) == 4 ? (unsafe.getInt("", 8L) & 0xFFFFFFFFL) << 3 : unsafe.getLong("", 8L);

        unsafe.putInt(klassPointer + MODIFIER_OFFSET, unsafe.getInt(klassPointer + MODIFIER_OFFSET) & ~Modifier.FINAL);
        unsafe.putInt(klassPointer + ACCESS_FLAG_OFFSET, unsafe.getInt(klassPointer + ACCESS_FLAG_OFFSET) & ~Modifier.FINAL);

        Class<?> clazz = new ASMClassLoader().load("me.yamakaja.unsafe.classhacking.SpecialStringImpl", SpecialStringClassGenerator.generateStringClass());

        SpecialString specialString = (SpecialString) clazz.getConstructor(String.class).newInstance("Hello World!");

        specialString.print();
    }

}
