package com.creative.gamesdk.common.utils_base.parse.project;

import android.content.Context;
import android.text.TextUtils;

import com.creative.gamesdk.common.utils_base.frame.google.gson.Gson;
import com.creative.gamesdk.common.utils_base.frame.google.gson.JsonSyntaxException;
import com.creative.gamesdk.common.utils_base.utils.FileUtils;
import com.creative.gamesdk.common.utils_base.utils.LogUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Administrator on 2019/2/13.
 * 项目管理类
 */

public class ProjectManager {
    private static final String TAG = "ProjectManager";
    public static String PROJECT_CONFIG = "Project_config.txt";

    /************************** 同步锁双重检测机制实现到单例模式(懒加载) ***************************/

    public static volatile ProjectManager projectManager;

    public static ProjectManager init(Context context) {
        if (projectManager == null) {
            synchronized (ProjectManager.class) {
                if (projectManager == null) {
                    projectManager = new ProjectManager(context);
                }
            }
        }
        return projectManager;
    }

    public static ProjectManager getInstance() {
        return projectManager;
    }

    /*****************************************************/

    private static HashMap<String, ProjectBeanList.ProjectBean> projectBeans = new HashMap<>();

    public ProjectManager(Context context) {
        parse(context, PROJECT_CONFIG);
    }

    private synchronized void parse(Context context, String projectConfig) {
        StringBuilder builder = FileUtils.readAssetsFile(context, projectConfig);
        String strContent = String.valueOf(builder);
        if (TextUtils.isEmpty(strContent)) {
            LogUtils.e(TAG, PROJECT_CONFIG + " parse empty");
            return;
        }
        Gson gson = new Gson();
        try {
            ProjectBeanList projectBeanList = gson.fromJson(strContent, ProjectBeanList.class);
            if (projectBeanList.getProject().isEmpty()) {
                LogUtils.e(TAG, PROJECT_CONFIG + " parse error");
                return;
            } else {
                for (ProjectBeanList.ProjectBean projectBean : projectBeanList.getProject()) {
                    projectBeans.put(projectBean.getProject_name(), projectBean);
                }
                LogUtils.debug_i(TAG, PROJECT_CONFIG + " parse \n" + projectBeans.toString());
            }
        } catch (JsonSyntaxException e) {
            LogUtils.e(TAG, PROJECT_CONFIG + " parse exception");
        }
    }

    private boolean hasLoaded;
    private HashMap<String, Project> projectLists = new HashMap<>();

    /**
     * 加载所有的project,可能存在多个project
     */
    public synchronized void loadAllProjects() {
        if (hasLoaded) {
            return;
        }
        HashMap<String, ProjectBeanList.ProjectBean> entries = projectBeans;
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            loadProject(key);
        }
        LogUtils.debug_i(TAG, "loadAllProject: " + projectLists.toString());
        hasLoaded = true;
    }

    /**
     * 加载单个project,project可能为空
     *
     * @param projectName
     * @throws RuntimeException
     */
    private Project loadProject(String projectName) throws RuntimeException {
        HashMap<String, ProjectBeanList.ProjectBean> entries = projectBeans;
        ProjectBeanList.ProjectBean projectBean = entries.get(projectName);
        if (projectBean == null) {
            LogUtils.debug_d(TAG, "loadProject: " + "[" + projectName + "]" + " dose not exit in " + PROJECT_CONFIG);
            return null;
        }
        Project project = null;
        project = projectBean.invokeGetInstance();
        if (project != null) {
            project.initProject();
            projectLists.put(projectName, project);
        }
        return project;
    }

    /**
     * 获取特定project,project可能为空
     *
     * @param projectName
     * @return
     */
    public Project getProject(String projectName) {
        Project project = null;
        if (!hasLoaded) {
            LogUtils.debug_d(TAG, "getProject: " + projectName + "has never load yet");
            return null;
        }
        HashMap<String, Project> entries = projectLists;
        project = entries.get(projectName);
        return project;
    }


    private static Project project;

    /**
     * 加载当前的project
     */
    public void loadProject() {
        ProjectBeanList.ProjectBean projectBean = projectBeans.get("project");
        if (projectBean == null) {
            LogUtils.debug_d(TAG, "loadProject: " + "project is not exit in the " + PROJECT_CONFIG);
            return;
        }
        Project project = null;
        project = projectBean.invokeGetInstance();
        if (project != null) {
            project.initProject();
            this.project = project;
        }
    }

    /**
     * 返回当前project项目
     *
     * @return
     */
    public Project getProject() {
        return this.project;
    }
}
