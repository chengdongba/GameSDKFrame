package com.creative.gamesdk.build.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Created by Administrator on 2019/2/28.
 * 操作文件类
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void fileCopy(String srcPath, String destPath, boolean isReplaseExisting) throws IOException {
        Path source = FileSystems.getDefault().getPath(srcPath);
        Path direct = FileSystems.getDefault().getPath(destPath);
        if (exist(destPath)) {
            if (isReplaseExisting) {
                Files.copy(source, direct, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            Files.copy(source, direct, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * smali file copy
     * @param appFirstSmaliPath
     * @param srcPath
     * @param destPath
     * @param filePathList 如果要拷贝的文件和filePathList文件列表路径下的某个文件有重复.
     *                     直接覆盖掉filePathList中的文件
     * @param putToMainDexClassList
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void smaliFileCopy(final String appFirstSmaliPath, String srcPath, String destPath,
                                     final List<String> filePathList, final List<String> putToMainDexClassList) throws IOException {
        final Path source = FileSystems.getDefault().getPath(srcPath);
        final Path destination = FileSystems.getDefault().getPath(destPath);
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(dir);
                String destPathStr = destination + File.separator + relativePath;
                Path destPath = FileSystems.getDefault().getPath(destPathStr);
                boolean pathExist = Files.exists(destPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
                if (!pathExist) {
                    Files.createDirectory(destPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = source.relativize(file);
                String destPathStr = destination + File.separator + relativePath;
                Path destPath = FileSystems.getDefault().getPath(destPathStr);
                if (null != putToMainDexClassList && !filePathList.isEmpty()) {
                    for (String classPath : putToMainDexClassList) {
                        String putMainDexPath = destination + File.separator + classPath + ".smali";
                        String appPutMainDexPath = appFirstSmaliPath + File.separator + classPath + ".smali";
                        if (relativePath.toString().equals(classPath + ".smali")) {
                            FileUtils.createFile(appPutMainDexPath, false);
                            FileUtils.copy(putMainDexPath, appPutMainDexPath, true);
                            return FileVisitResult.CONTINUE;
                        }
                    }
                }

                boolean isExist = false;
                if (null!=filePathList){
                    for (String filePath : filePathList) {
                        String destFilePath = filePath + File.separator + relativePath;
                        if (exist(destFilePath)){
                            isExist=true;
                            Files.copy(file,FileSystems.getDefault().getPath(destFilePath),StandardCopyOption.REPLACE_EXISTING);
                            break;
                        }
                    }
                }
                if (!isExist){
                    Files.copy(file,destPath,StandardCopyOption.REPLACE_EXISTING);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                if (null!=exc){
                    exc.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * whether file exist
     *
     * @param filePath
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean exist(String filePath) {

        Path path = FileSystems.getDefault().getPath(filePath);

        return Files.exists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
    }

    /**
     * createFile
     *
     * @param path
     * @param isDeleteIfExist
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createFile(String path, boolean isDeleteIfExist) throws IOException {
        Path filePath = FileSystems.getDefault().getPath(path);
        boolean fileExists = Files.exists(filePath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (fileExists) {
            if (isDeleteIfExist) {
                delete(path);
            } else {
                return;
            }
        }

        File f = new File(path);
        FileUtils.createDirectoriesIfNonExists(f.getParentFile().getAbsolutePath());
        Files.createFile(filePath);
    }

    /**
     * create directory if non exists
     *
     * @param path
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createDirectoriesIfNonExists(String path) throws IOException {
        Path destPath = FileSystems.getDefault().getPath(path);
        boolean isExist = Files.exists(destPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (isExist) {
            return;
        }
        Files.createDirectory(destPath);
    }

    /**
     * delete file
     *
     * @param path
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void delete(String path) throws IOException {
        Path source = FileSystems.getDefault().getPath(path);
        Files.walkFileTree(source, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                if (null != exc) {
                    exc.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        Files.deleteIfExists(source);
    }

    /**
     * copy file
     *
     * @param srcpath
     * @param destPath
     * @param isDeleteExisting
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void copy(String srcpath, String destPath, final boolean isDeleteExisting) throws IOException {
        final Path source = FileSystems.getDefault().getPath(srcpath);
        final Path destination = FileSystems.getDefault().getPath(destPath);
        Files.walkFileTree(source, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativizePath = source.relativize(dir);
                String destPathStr = destination + File.separator + relativizePath;
                Path destPath = FileSystems.getDefault().getPath(destPathStr);
                boolean pathExists = Files.exists(destPath);
                if (!pathExists) {
                    Files.createDirectory(destPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativizePath = source.relativize(file);
                String destPathStr = destination + File.separator + relativizePath;
                Path destPath = FileSystems.getDefault().getPath(destPathStr);
                boolean pathExists = Files.exists(destPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
                if (pathExists && !isDeleteExisting) {
                    return FileVisitResult.CONTINUE;
                }
                Files.copy(file, destPath, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                if (null != exc) {
                    exc.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createDirectories(String path, boolean isDeleteIfExists) throws IOException {
        Path destPath = FileSystems.getDefault().getPath(path);
        boolean isExists = Files.exists(destPath,new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (isExists){
            if (isDeleteIfExists){
                delete(path);
            }else {
                return;
            }
        }

        Files.createDirectory(destPath);
    }

    /**
     * Create directory
     *
     * @param path path
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createDirectory(String path) throws IOException {
        Path destPath = FileSystems.getDefault().getPath(path);
        Files.createDirectory(destPath);
    }

}
