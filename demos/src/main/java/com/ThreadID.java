package com;

import static com.VMTools.getTID;

/**
 * @author vladimir.dolzhenko
 * @since 2017-01-23
 */
public class ThreadID {
    public static void main(String[] args) throws Exception {
        System.out.printf("0x%016X", getTID(Thread.currentThread().getName()));
    }


}
