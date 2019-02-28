package com.creative.gamesdk.build.tools;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.creative.gamesdk.build.bean.Project;
import com.creative.gamesdk.build.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2019/2/28.
 * <p>
 * 模拟服务器同步过程
 * 一般代码都是放到svn或者git上托管,模拟服务器存储地址
 */

public class ServerTool {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void DownServerResource(List<Project> projectList, String checkOutDir) throws IOException {
        //获取用户当前工作目录
        String root_path = System.getProperty("user.dir");
        Iterator<Project> iterator = projectList.iterator();
        while (iterator.hasNext()) {
            Project project = iterator.next();

            //获取源码目录
            String projectName = project.getName();
            String projectUrl = project.getUrl();
            String projectResource = root_path + File.separator + projectUrl;

            //工作目录
            String destPath = checkOutDir + File.separator + projectName;
            Path destDirPath = FileSystems.getDefault().getPath(destPath);

            boolean isDestDirExists = Files.exists(destDirPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
            if (isDestDirExists) {
                FileUtils.delete(destPath);
            }

            FileUtils.copy(projectResource, destPath, false);
            project.setPath(destPath);

        }
    }

}
