package com;

/*
Benchmark             (size)  Mode  Cnt         Score        Error  Units
BaseHashLoad.fillMap       1  avgt    5        23.551 ±      0.846  ns/op
BaseHashLoad.fillMap      50  avgt    5       456.789 ±     19.035  ns/op
BaseHashLoad.fillMap     100  avgt    5       868.598 ±     18.300  ns/op
BaseHashLoad.fillMap     500  avgt    5      4619.051 ±    271.170  ns/op
BaseHashLoad.fillMap    1000  avgt    5      9995.946 ±    196.598  ns/op
BaseHashLoad.fillMap   10000  avgt    5    218728.376 ±  33923.432  ns/op
BaseHashLoad.fillMap   25000  avgt    5    810497.804 ± 266457.870  ns/op
BaseHashLoad.fillMap   50000  avgt    5   2902067.658 ± 251401.508  ns/op
BaseHashLoad.fillMap   75000  avgt    5   5452701.212 ± 512799.007  ns/op
BaseHashLoad.fillMap  100000  avgt    5   9914771.215 ± 128615.151  ns/op
BaseHashLoad.fillMap  150000  avgt    5   6728727.817 ±  81477.532  ns/op
BaseHashLoad.fillMap  200000  avgt    5  13289876.075 ± 303002.251  ns/op
 */

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author vladimir.dolzhenko@gmail.com
 * @since 2017-04-06
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 5000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class BaseHashLoad {

    private String[] keys;

    @Param( {"1", "50", "100", "500", "1000", "10000", "25000", "50000", "75000", "100000", "150000", "200000"})
    private int size;
    private Map<String, String> map;

    @Setup
    public void setup() throws Exception {
        Set<String> words = new HashSet<>();
        try(Scanner scanner = new Scanner(new File("/usr/share/dict/words"))){
            while(scanner.hasNext()){
                words.add(scanner.next());
                if (words.size() > size){
                    break;
                }
            }
        }
        keys = words.toArray(new String[words.size()]);
        map = new HashMap<>(size);
    }

    @Benchmark
    public Map fillMap() {
        for (String key : keys) map.put(key, key);

        return map;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BaseHashLoad.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
