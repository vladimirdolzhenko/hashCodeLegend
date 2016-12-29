package com;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;

/**
 * to run with <code>-XX:hashCode=4</code>
 *
 * @author vladimir.dolzhenko
 * @since 2016-05-31
 */
public class ObjectMemoryDump {
    static final VirtualMachine vm = VM.current();

    public static void main(String[] args) {
        final Object o = new Object();
        final long[] longs0 = new long[0];
        final long[] longs = new long[] {0xCAFEBEBEDEADBEAFL};

        System.out.println(vm.details());

        long freeMemory = Runtime.getRuntime().freeMemory();
        System.out.println(freeMemory);

        printValues(new String[][] {
                print(null),
                print(o),
                print(o.hashCode()),
                print(longs0),
                print(longs)
        });
    }

    public static void printValues(String[][] strings) {
        final int[] maxLengths =
                IntStream.range(0, strings[0].length)
                        .map(c ->
                                IntStream.range(0, strings.length)
                                        .map(r -> strings[r][c].length())
                                        .max()
                                        .getAsInt())
                        .toArray();

        IntStream.range(0, strings.length).forEach(
                r -> {
                    IntStream.range(0, strings[r].length)
                            .forEach(c ->
                                    System.out.print(strings[r][c]
                                            + IntStream
                                            .range(strings[r][c].length(), maxLengths[c] + 4)
                                            .mapToObj(k -> " ")
                                            .collect(Collectors.joining(""))
                                    ));
                    System.out.println();
                }
        );
    }

    public static String[] print(Object o) {
        return print(o, true);
    }

    public static String[] print(Object o, boolean touchSysHashCode) {
        if (o == null) {
            return new String[] {
                    "class",
                    "address",
                    "next addr",
                    "id hashcode",
                    "size",
                    "dump"
            };
        }
        if (touchSysHashCode) {
            // otherwise object header would not have hash code
            int i = o.hashCode();
        }
        byte[] bytes = dump(o);

        long address = getAddress(o);
        final String sysHashCode;
        if (touchSysHashCode) {
            sysHashCode = String.format("0x%08X", System.identityHashCode(o));
        } else {
            sysHashCode = "N/A";
        }
        return new String[] {
                o.getClass().getSimpleName(),
                String.format("0x%08X", address),
                String.format("0x%08X", address + bytes.length),
                sysHashCode,
                String.format("%d", bytes.length),
                dumpToString(bytes)
        };
    }

    public static long getAddress(Object o) {
        return vm.addressOf(o);
    }

    public static long sizeOf(Object obj) {
        return vm.sizeOf(obj);
    }

    public static byte[] dump(Object obj) {
        int len = (int) sizeOf(obj);
        byte[] bytes = new byte[len];

        // dump based on native order
        final ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());

        for (int offset = 0; offset < len; offset += Integer.SIZE / Byte.SIZE) {
            buffer.putInt(vm.getInt(obj, offset));
        }

        return bytes;
    }

    public static String dumpToString(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02X", bytes[i] & 0xFF))
                .collect(Collectors.joining(" "));
    }
}