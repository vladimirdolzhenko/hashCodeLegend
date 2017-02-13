package com;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-25
 */
public class VMTools {

    static void checkVMHashCodeAsAddress() {
        checkVMIntegerOption("hashCode", 4);
    }

    static void checkVMBooleanOption(String optionName, boolean value) {
        final String actualValue = getVMOption(optionName);
        if (Boolean.parseBoolean(actualValue) != value) {
            throw new IllegalStateException(String.format("expected -XX:%s%s", value ? "+" : "-", optionName));
        }
    }

    static void checkVMIntegerOption(String optionName, int value) {
        final String actualValue = getVMOption(optionName);
        if (Integer.parseInt(actualValue) != value) {
            throw new IllegalStateException(String.format("expected -XX:%s=%d", optionName, value));
        }
    }

    static String getVMBooleanFlag(String optionName) {
        final String actualValue = getVMOption(optionName);
        boolean parseBoolean = Boolean.parseBoolean(actualValue);
        return "-XX:" + (parseBoolean ? "+" : "-") + optionName;
    }

    static String getVMOption(String key) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbean = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
            CompositeDataSupport val = (CompositeDataSupport) server.invoke(mbean,
                                                                            "getVMOption",
                                                                            new Object[] {key},
                                                                            new String[] {"java.lang.String"});
            return val.get("value").toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static long getTID(String threadName) {
        final String threadPrint = threadPrint();

        final String threadPrefix = '"' + threadName + "\" ";

        // look up for a line like:
        // "main" #1 prio=5 os_prio=0 tid=0x0000000002349000 nid=0x8ec4 waiting on condition [0x000000000255e000]

        for (String line : threadPrint.split("\n")) {
            if (line.startsWith(threadPrefix)) {
                final String tidPrefix = " tid=0x";
                final int st = line.indexOf(tidPrefix);
                if (st > 0) {
                    final int end = line.indexOf(' ', st + tidPrefix.length());
                    final String substring = line.substring(st + tidPrefix.length(), end);
                    final long tid = Long.parseLong(substring, 16);
                    return tid;
                }
            }
        }

        throw new IllegalArgumentException("no thread " + threadName);
    }

    static String threadPrint() {
        try {
            ObjectName objectName = new ObjectName("com.sun.management:type=DiagnosticCommand");
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            return (String) mbeanServer.invoke(objectName, "threadPrint",
                                               new Object[] {new String[] {}},
                                               new String[] {String[].class.getName()});
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
