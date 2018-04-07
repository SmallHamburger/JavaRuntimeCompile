package org.jeson.java.compile;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, byte[]> classBytes = new HashMap<>();

    MemoryJavaFileManager(StandardJavaFileManager fileObject) {
        super(fileObject);
    }

    @Override
    public void close() {
        classBytes.clear();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return new MemoryJavaFileObject(classBytes, className, null);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    Iterable<? extends JavaFileObject> getJavaSourceFile(Iterable<? extends File> files) {
        return ((StandardJavaFileManager) fileManager).getJavaFileObjectsFromFiles(files);
    }

    JavaFileObject getJavaSourceFile(String className, String classCode) {
        return new MemoryJavaFileObject(classBytes, className, classCode);
    }

    Map<String, byte[]> getClassBytes() {
        return classBytes;
    }

    static class MemoryJavaFileObject extends SimpleJavaFileObject {

        private final String name;
        private final String code;
        private Map<String, byte[]> classBytes;

        private MemoryJavaFileObject(Map<String, byte[]> classBytes, String name, String code) {
            super(URI.create("string:///" + name), code == null ? Kind.CLASS : Kind.SOURCE);
            this.name = name;
            this.code = code;
            this.classBytes = classBytes;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            if (code == null) {
                return super.getCharContent(ignoreEncodingErrors);
            } else {
                return CharBuffer.wrap(code);
            }
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    super.close();
                    if (classBytes != null) {
                        classBytes.put(name, ((ByteArrayOutputStream) out).toByteArray());
                    }
                }
            };
        }
    }

}
