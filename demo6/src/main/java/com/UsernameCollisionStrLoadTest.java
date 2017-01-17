package com;

/*

java 8

Benchmark                                (size)  Mode  Cnt              Score   Error  Units
UsernameCollisionStrLoadTest.fillCmpMap       1  avgt                  11.507          ns/op
UsernameCollisionStrLoadTest.fillCmpMap      50  avgt                5479.002          ns/op
UsernameCollisionStrLoadTest.fillCmpMap     100  avgt               11609.386          ns/op
UsernameCollisionStrLoadTest.fillCmpMap     500  avgt               91693.993          ns/op
UsernameCollisionStrLoadTest.fillCmpMap    1000  avgt              252276.953          ns/op
UsernameCollisionStrLoadTest.fillCmpMap   10000  avgt             3686658.223          ns/op
UsernameCollisionStrLoadTest.fillCmpMap   25000  avgt            13344518.934          ns/op
UsernameCollisionStrLoadTest.fillCmpMap   50000  avgt            28947167.714          ns/op
UsernameCollisionStrLoadTest.fillCmpMap   75000  avgt            48755767.714          ns/op
UsernameCollisionStrLoadTest.fillCmpMap  100000  avgt            66062626.125          ns/op
UsernameCollisionStrLoadTest.fillCmpMap  150000  avgt           114163048.889          ns/op
UsernameCollisionStrLoadTest.fillCmpMap  200000  avgt           160034683.857          ns/op
UsernameCollisionStrLoadTest.fillMap          1  avgt                  11.276          ns/op
UsernameCollisionStrLoadTest.fillMap         50  avgt               19305.912          ns/op
UsernameCollisionStrLoadTest.fillMap        100  avgt               74312.588          ns/op
UsernameCollisionStrLoadTest.fillMap        500  avgt             2253508.317          ns/op
UsernameCollisionStrLoadTest.fillMap       1000  avgt             9893163.647          ns/op
UsernameCollisionStrLoadTest.fillMap      10000  avgt          1683110242.000          ns/op
UsernameCollisionStrLoadTest.fillMap      25000  avgt         12880649807.000          ns/op
UsernameCollisionStrLoadTest.fillMap      50000  avgt         92109322977.000          ns/op
UsernameCollisionStrLoadTest.fillMap      75000  avgt        300222705532.000          ns/op
UsernameCollisionStrLoadTest.fillMap     100000  avgt        632434197049.000          ns/op
UsernameCollisionStrLoadTest.fillMap     150000  avgt       1634156408105.000          ns/op
UsernameCollisionStrLoadTest.fillMap     200000  avgt       3024079023237.000          ns/op


 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static com.UsernameCollisionPerfTest.load;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-07
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 0, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 1, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@State(Scope.Benchmark)
public class UsernameCollisionStrLoadTest {

    private Str[] keys;
    private StrCmp[] cmpKeys;

    @Param( {"1", "50", "100", "500", "1000", "10000", "25000", "50000", "75000", "100000", "150000", "200000"})
    private int size;
    private Map<Str, Str> map;
    private Map<StrCmp, StrCmp> cmpMap;

    @Setup
    public void setup() throws Exception {
        final String[] strings = load(size);
        keys = new Str[strings.length];
        cmpKeys = new StrCmp[strings.length];
        for (int i = 0; i < strings.length; i++) {
            keys[i] = new Str(strings[i]);
            cmpKeys[i] = new StrCmp(strings[i]);
        }
        map = new HashMap<Str, Str>(size);
        cmpMap = new HashMap<StrCmp, StrCmp>(size);
    }

    @Benchmark
    public Map fillMap() {
        for (Str key : keys) {
            map.put(key, key);
        }

        return map;
    }

    @Benchmark
    public Map fillCmpMap() {
        for (StrCmp key : cmpKeys) {
            cmpMap.put(key, key);
        }

        return map;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UsernameCollisionStrLoadTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}