package me.yamakaja.unsafe.classhacking;

/**
 * Created by Yamakaja on 9/7/17.
 */
public class ASMClassLoader extends ClassLoader {


    public ASMClassLoader() {
        super(ASMClassLoader.class.getClassLoader());
    }

    public Class<?> load(String name, byte[] data) {
        return this.defineClass(name, data, 0, data.length);
    }

}
