package com;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Don't run with <code>-XX:+UseParallelGC</code>
 * https://twitter.com/gvsmirnov/status/817407819402137600
 *
 * Use <code>-XX:+UseSerialGC</code>, <code>-XX:+UseConcMarkSweepGC</code>
 * or <code>-XX:+UseG1GC</code>
 *
 * @author vladimir.dolzhenko@gmail.com
 * @since 2017-02-15
 */
public class Oom {
    public static void main(String[] args) {
        List list = new ArrayList();

        while (true)
            list.add(new Object());
    }
}
