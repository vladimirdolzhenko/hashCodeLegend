package com;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;

import static com.VMTools.getVMBooleanFlag;

/**
 * @author vladimir.dolzhenko@gmail.com
 * @since 2017-02-25
 */
public class ObjectSize {
    static VirtualMachine vm = VM.current();

    public static void main(String[] args) {
        System.out.println(getVMBooleanFlag("UseCompressedOops"));
        System.out.println(info(new Pair()));
        System.out.println(info(new Pair2()));

        System.out.println(ClassLayout.parseClass(Pair.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Pair2.class).toPrintable());
    }

    static String info(Object o){
        return o.getClass().getSimpleName() + "\tsize: " + vm.sizeOf(o) + " b";
    }

    static class Pair<F, S> {
        F first;
        S second;
    }

    static class Pair2<F, S> {
        F first;
        S second;
        int hashCode;
    }
}
