package org.jeson.java.compile;

import javax.lang.model.SourceVersion;
import javax.tools.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class StringJavaCompiler implements JavaCompiler {

    private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

    Map<String, byte[]> compile(Iterable<? extends File> files) {
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(getStandardFileManager(null, null, null));
        return compile(fileManager, fileManager.getJavaSourceFile(files));
    }

    Map<String, byte[]> compile(String name, String code) {
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(getStandardFileManager(null, null, null));
        return compile(fileManager, Arrays.asList(fileManager.getJavaSourceFile(name, code)));
    }

    private Map<String, byte[]> compile(MemoryJavaFileManager fileManager, Iterable<? extends JavaFileObject> javaFileObjects) {
        try {
            CompilationTask task = getTask(null, fileManager, null, null, null, javaFileObjects);
            Boolean isSuccess = task.call();
            if (isSuccess != null && isSuccess) {
                return new HashMap<>(fileManager.getClassBytes());
            } else {
                throw new RuntimeException("unknown error");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileManager != null) {
                fileManager.close();
            }
        }
    }

    @Override
    public CompilationTask getTask(Writer out, JavaFileManager fileManager, DiagnosticListener<? super JavaFileObject> diagnosticListener, Iterable<String> options, Iterable<String> classes, Iterable<? extends JavaFileObject> compilationUnits) {
        return JAVA_COMPILER.getTask(out, fileManager, diagnosticListener, options, classes, compilationUnits);
    }

    @Override
    public StandardJavaFileManager getStandardFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener, Locale locale, Charset charset) {
        return JAVA_COMPILER.getStandardFileManager(diagnosticListener, locale, charset);
    }

    @Override
    public int isSupportedOption(String option) {
        return JAVA_COMPILER.isSupportedOption(option);
    }

    @Override
    public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
        return JAVA_COMPILER.run(in, out, err, arguments);
    }

    @Override
    public Set<SourceVersion> getSourceVersions() {
        return JAVA_COMPILER.getSourceVersions();
    }
}
