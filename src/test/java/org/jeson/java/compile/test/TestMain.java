package org.jeson.java.compile.test;


import org.jeson.java.compile.RuntimeClassLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class TestMain {

    private static final String JAVA_SOURCE_CODE = ""
            + "package org.jeson;"
            + "\n"
            + "public class Test {"
            + "\n"
            + "    static {"
            + "\n"
            + "        System.out.println(\"Test0 loaded\");"
            + "\n"
            + "    }"
            + "\n"
            + "    public static class SubTest{"
            + "\n"
            + "        static {"
            + "\n"
            + "            System.out.println(\"SubTest0 loaded\");"
            + "\n"
            + "        }"
            + "\n"
            + "    }"
            + "\n"
            + "}";

    private RuntimeClassLoader mRuntimeClassLoader;

    @Before
    public void setUp() {
        mRuntimeClassLoader = new RuntimeClassLoader();
    }

    @Test
    public void defineStringInputStreamClassTest() throws Exception {
        Map<String, Class<?>> classMapper = mRuntimeClassLoader.defineClass("Test2.java", TestMain.class.getResourceAsStream("/Test2.java"));
        for (Class<?> clazz : classMapper.values()) {
            clazz.newInstance();
        }
    }

    @Test
    public void defineSingleClassTest() throws Exception {
        Map<String, Class<?>> classMapper = mRuntimeClassLoader.defineClass("Test", JAVA_SOURCE_CODE);
        for (Class<?> clazz : classMapper.values()) {
            clazz.newInstance();
        }
    }

    @Test
    public void defineMultiClassTest() throws Exception {
        Map<String, Class<?>> classMapper = mRuntimeClassLoader.defineClass(Arrays.asList(new File("Test.java")));
        for (Class<?> clazz : classMapper.values()) {
            clazz.newInstance();
        }
    }

}
