package com;

import java.lang.reflect.Field;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;
import sun.misc.Unsafe;

import static com.VMTools.checkVMBooleanOption;

/**
 * <code>-XX:-UseCompressedOops</code>
 *
 * @author vladimir.dolzhenko
 * @since 2017-01-25
 */
public class UnsafeDemo {

    public static void main(String[] args) throws Exception {
        final Object object = new Object();

        final VirtualMachine vm = VM.current();
        System.out.println(ClassLayout.parseClass(A.class).toPrintable());
        System.out.printf("address: 0x%016X%n", vm.addressOf(object));

        System.out.printf("address: 0x%016X\n", getAddress(object));

    }

    public static class A {
        boolean f;
    }

    public static long getAddress(final Object object) throws Exception {
        checkVMBooleanOption("UseCompressedOops", false);

        Unsafe unsafe = fetchUnsafe();

        return unsafe.getLong(new Object[]{object},
                unsafe.arrayBaseOffset(Object[].class));
    }

    private static Unsafe fetchUnsafe() throws Exception {
        final Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }

}
