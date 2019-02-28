package com.creative.gamesdk.build.tools;

/**
 * Created by Administrator on 2019/2/28.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaTool {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void compile(String sourcePath, String classPath, String classFileOutPutPath, String logOutPutPath) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Path source = FileSystems.getDefault().getPath(sourcePath);
        final List<String> javaFilePathList = new ArrayList<>();
        Files.walkFileTree(source, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith("java")) {
                    javaFilePathList.add(file.toAbsolutePath().toString());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        //编译日志输出到文件
        FileWriter fileWriter = null;
        StandardJavaFileManager standardJavaFileManager = null;
        boolean compileSuccessed = true;
        if (!TextUtils.isEmpty(logOutPutPath)) {
            try {
                fileWriter = new FileWriter(logOutPutPath, true);

                List<String> optionList = new ArrayList<>();
                optionList.addAll(Arrays.asList("-classpath", classPath));
                optionList.addAll(Arrays.asList("-d", classFileOutPutPath));
                standardJavaFileManager = compiler.getStandardJavaFileManager(null, null, null);
                Iterable fileObjects = standardJavaFileManager.getJavaFileObjectsFromStrings(javaFilePathList);
                JavaCompiler.CompilationTask compilationTask = compiler.getTask(fileWriter, null, null, optionList, null, fileObjects);
                compileSucceed = compilationTask.call();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    fileWriter.close();
                }
                if (standardJavaFileManager != null) {
                    standardJavaFileManager.close();
                }
            }

            if (!compileSuccessed) {
                throw new Exception("compile .java to .class failed");
            }
        }
    }

    /**
     * 文件类打为jar包
     * @param classFilePath
     * @param jarOutPutPath
     * @param logOutPutPath
     */
    public static void classFilesToJar(String classFilePath,String jarOutPutPath,String logOutPutPath){
        List<String> arguments = new ArrayList<>();
        arguments.add("jar");
        arguments.add("cvf");
        arguments.add("jar");
        arguments.add(jarOutPutPath);
        arguments.add("-C");
        arguments.add(classFilePath);
        arguments.add(".");
        Shell.execute(arguments,logOutPutPath);
    }

}
