package com.creative.gamesdk.build;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.android.manifmerger.ManifestMerger2;
import com.android.manifmerger.MergingReport;
import com.android.manifmerger.XmlDocument;
import com.android.utils.StdLogger;
import com.creative.gamesdk.build.bean.ErrorMsg;
import com.creative.gamesdk.build.bean.Project;
import com.creative.gamesdk.build.tools.JavaTool;
import com.creative.gamesdk.build.tools.ProGuardTool;
import com.creative.gamesdk.build.tools.ServerTool;
import com.creative.gamesdk.build.utils.FileUtils;
import com.creative.gamesdk.build.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by Administrator on 2019/2/28.
 * 混淆jarTask
 */

public class BuildJarTask implements Runnable {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        //创建输出路径
        String outputPath;
        try {
            Config.getInstance().loadConfig();
            outputPath = Config.getInstance().getOutPutPath();
            com.creative.gamesdk.build.utils.FileUtils.createDirectories(outputPath, true);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //同步资源
        List<Project> projectList;
        try {
            projectList = Config.getInstance().getProject();
            if (null == projectList || projectList.isEmpty()) {
                Log.e("dqchen", "project list is empty");
                return;
            }

            String workspacePath = outputPath + File.separator + "workspace";
            FileUtils.createDirectory(workspacePath);
            ServerTool.DownServerResource(projectList, workspacePath);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //build jar
        String jarName = Config.getInstance().getJarName();
        String jarVersion = Config.getInstance().getJarVersion();
        String buildJar = jarName + "_" + jarVersion;

        String jarOutPutPath = outputPath + File.separator + "jar_out";
        String jarFileOutPutPath = jarOutPutPath + File.separator + "jar";
        ErrorMsg errorMsg = buildJar(projectList, jarFileOutPutPath);
    }

    /**
     * 编译生成混淆jar包过程
     *
     * @param projectList   工程配置
     * @param jarOutPutPath jar包输出路径
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ErrorMsg buildJar(List<Project> projectList, String jarOutPutPath) {

        File jarFile = new File(jarOutPutPath);
        String outPutPath = jarFile.getParent();

        //创建一个Temp工程
        Project tempBuildProject = new Project();
        String projectName = "tempBuildProject";
        String tempBuildProjectPath = outPutPath + File.separator + projectName;
        tempBuildProject.setName(projectName);
        tempBuildProject.setPath(tempBuildProjectPath);

        //创建temp工程目录 java 和 libs
        String projectSrcPath = tempBuildProjectPath + File.separator + Project.JAVA_REALTIVE_PATH;
        try {
            FileUtils.createDirectoriesIfNonExists(outPutPath);
            FileUtils.createDirectoriesIfNonExists(tempBuildProjectPath);
            FileUtils.createDirectoriesIfNonExists(projectSrcPath);
        } catch (IOException e) {
            new ErrorMsg(Utils.ERROR, e.getMessage(), e);
        }

        //将各个项目java文件下的源文件拷贝到tempBuildProject的src文件下
        for (Project project : projectList) {
            String tempProjectBuildPath = project.getPath() + File.separator + Project.JAVA_REALTIVE_PATH;
            if (FileUtils.exist(tempProjectBuildPath)) {
                try {
                    FileUtils.copy(tempBuildProjectPath, projectSrcPath, false);
                } catch (IOException e) {
                    new ErrorMsg(Utils.ERROR, e.getMessage(), e);
                }
            }
        }

        //.java and .jar compile to .class
        String classPath = getClassPath(tempBuildProject,projectList);
        String classFilesOutputPath = tempBuildProjectPath + File.separator + "classes";
        try {
            FileUtils.createDirectory(classFilesOutputPath);
            JavaTool.compile(projectSrcPath, classPath, classFilesOutputPath, null);
        }  catch (Exception e) {
            new ErrorMsg(Utils.ERROR,"构建SDK-编译 .java to .class 出错",e);
        }

        //.class compile to .jar
        String noProguardJar = outPutPath + File.separator + "no_proguard.jar";
        try{
            JavaTool.classFilesToJar(classFilesOutputPath, noProguardJar, null);
        }catch (Exception e){
            return new ErrorMsg(Utils.ERROR, "构建SDK-编译.class打包jar出错", e);
        }

        //proguard .jar
        try{

            String mapping = outPutPath + File.separator + "proguard_mapping.txt";
            String logging = outPutPath + File.separator + "proguard_log.txt";
            String root_path = System.getProperty("user.dir");
            String proJarPath = root_path + File.separator + "GameSDKBuildJarTool" + File.separator + "libs"
                    + File.separator + "proguard.jar";
            String proguardConfigFilePath = Config.getInstance().get_config_file_path("proguard_config.pro");
            ProGuardTool.run(proJarPath, noProguardJar, classPath, proguardConfigFilePath, mapping, logging, jarOutPutPath);

        }catch (Exception e){
            return new ErrorMsg(Utils.ERROR, "构建SDK-混淆jar出错", e);
        }

        return new ErrorMsg(Utils.OK, "ok");
    }

    /**
     * 合并工程资源文件过程
     * @param resourceOutputPath
     * @param projectList
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buildResourceFile(String resourceOutputPath, List<Project> projectList) throws IOException, ManifestMerger2.MergeFailureException {

        //copy libs,assets,res to library project
        String libsPath = resourceOutputPath + File.separator + Project.LIBS_FILE;
        String assetsPath = resourceOutputPath + File.separator + Project.ASSETS_FILE;
        String resPath = resourceOutputPath + File.separator + Project.RES_FILE;
        String jniLibsFilePath = resourceOutputPath+File.separator+Project.JNI_LIBS_FILE;
        String manifestPath = resourceOutputPath+File.separator+Project.ANDROID_MANIFEST_FILE;

        for (Project project : projectList) {
            //copy assets
            String projectAssetsPath = project.getPath()+File.separator+Project.ASSETS_RELATIVE_PATH;
            if (FileUtils.exist(projectAssetsPath)){
                FileUtils.createDirectoriesIfNonExists(assetsPath);
                FileUtils.copy(projectAssetsPath,assetsPath,false);
            }

            //copy resPath
            String projectResPath = project.getPath()+File.separator+Project.RES_RELATIVE_PATH;
            if (FileUtils.exist(projectResPath)){
                FileUtils.createDirectoriesIfNonExists(resPath);
                FileUtils.copy(projectResPath,resPath,false);
            }

            /*copy jni libs*/
            String projectJniLibsPath = project.getPath() + File.separator + Project.JNI_LIBS_RELATIVE_PATH;
            if (FileUtils.exist(projectJniLibsPath)){
                FileUtils.createDirectoriesIfNonExists(jniLibsFilePath);
                FileUtils.copy(projectJniLibsPath, jniLibsFilePath, false);
            }

            /*copy libs*/
            String projectLibsPath = project.getPath() + File.separator + Project.LIBS_RELATIVE_PATH;
            if (FileUtils.exist(projectLibsPath)){
                FileUtils.createDirectoriesIfNonExists(libsPath);
                FileUtils.copy(projectLibsPath, libsPath, false);
            }

            //merge manifest
            String projectManifestPath = project.getPath()+File.separator+Project.ANDROID_MANIFEST_RELATIVE_PATH;
            if (FileUtils.exist(projectManifestPath)){
                FileUtils.createDirectoriesIfNonExists(manifestPath);
                FileUtils.copy(projectManifestPath,manifestPath,false);
            }else {
                StdLogger stdLogger = new StdLogger(StdLogger.Level.ERROR);
                ManifestMerger2.Invoker manifestMerge = ManifestMerger2.newMerger(new File(manifestPath),stdLogger, ManifestMerger2.MergeType.APPLICATION);
                manifestMerge.addLibraryManifest(new File(projectManifestPath));
                manifestMerge.withFeatures(ManifestMerger2.Invoker.Feature.REMOVE_TOOLS_DECLARATIONS);
                MergingReport mergingReport = manifestMerge.merge();
                XmlDocument document = mergingReport.getMergedDocument().get();
                Files.write(FileSystems.getDefault().getPath(manifestPath),document.prettyPrint().getBytes("UTF-8"), StandardOpenOption.WRITE);
                switch (mergingReport.getResult()){
                    case WARNING:
                        mergingReport.log(stdLogger);
                        break;
                    case SUCCESS:
                        break;
                    case ERROR:
                        mergingReport.log(stdLogger);
                        throw new RuntimeException(mergingReport.getReportString());
                        default:
                           throw new RuntimeException("unHandler result type" + mergingReport.getResult());
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getClassPath(Project project, List<Project> projectList) {
        String classPath = "";

        //获取工程jar路径
        for (Project dependProject : projectList) {
            String projectPath = dependProject.getPath();
            String projectLibsPath = projectPath + File.separator + Project.LIBS_RELATIVE_PATH;
            if (FileUtils.exist(projectLibsPath)) {
                String jarPathSet = Utils.getJarPathSet(projectLibsPath, Utils.OS_SEMICOLONE);
                if (!TextUtils.isEmpty(jarPathSet)) {
                    if (!TextUtils.isEmpty(classPath)) {
                        classPath += Utils.OS_SEMICOLONE;
                    } else {
                        classPath += jarPathSet;
                    }
                }
            }
        }

        //获取工程源码路径
        String projectJavaPath = project.getPath() + File.separator + Project.JAVA_REALTIVE_PATH;
        if (FileUtils.exist(projectJavaPath)) {
            if (!TextUtils.isEmpty(classPath)) {
                classPath += Utils.OS_SEMICOLONE;
            } else {
                classPath += projectJavaPath;
            }
        }

        //获取SDK版本 android.jar
        if (!TextUtils.isEmpty(classPath)) {
            classPath += Utils.OS_SEMICOLONE;
        }

        classPath += getMinSdkVersionPath();

        return classPath;
    }

    private String getMinSdkVersionPath() {
        String androidSDKPath = Config.getInstance().getAndroidSdkPath();
        String androidJarPath = androidSDKPath + File.separator + "platforms"
                + File.separator + Config.getInstance().getTargetSdkVersion() + File.separator + "android.jar";
        return androidJarPath;
    }
}
