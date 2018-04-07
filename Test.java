package org.jeson.test;

public class Test {

    static {
        System.out.println("UpTest1 loaded");
    }

    public static class SubTest{

        static {
            System.out.println("SubTest1 loaded");
        }
    }

}
