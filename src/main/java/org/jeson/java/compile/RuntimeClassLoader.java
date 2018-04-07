package org.jeson.java.compile;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RuntimeClassLoader extends URLClassLoader {
    private Map<String, byte[]> classBytes = new HashMap<>();

    public RuntimeClassLoader() {
        this(null);
    }

    public RuntimeClassLoader(Map<String, byte[]> classBytes) {
        super(new URL[0], RuntimeClassLoader.class.getClassLoader());
        if (classBytes != null) {
            this.classBytes.putAll(classBytes);
        }
    }

    public Map<String, Class<?>> defineClass(String javaFileName, InputStream stringCodeInputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stringCodeInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return defineClass(javaFileName, sb.toString());
    }

    public Map<String, Class<?>> defineClass(String javaFileName, String code) {
        Map<String, byte[]> classBytesMap = new StringJavaCompiler().compile(javaFileName.endsWith(".java") ? javaFileName : javaFileName + ".java", code);
        classBytes.putAll(classBytesMap);
        return findClass(classBytesMap.keySet());
    }

    public Map<String, Class<?>> defineClass(Iterable<? extends File> files) {
        Map<String, byte[]> classBytesMap = new StringJavaCompiler().compile(files);
        classBytes.putAll(classBytesMap);
        return findClass(classBytesMap.keySet());
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.get(name);
        if (bytes == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);
        return defineClass(name, bytes, 0, bytes.length);
    }

    private Map<String, Class<?>> findClass(Set<String> className) {
        try {
            Map<String, Class<?>> classMapper = new HashMap<>();
            for (String name : className) {
                classMapper.put(name, findClass(name));
            }
            return classMapper;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
