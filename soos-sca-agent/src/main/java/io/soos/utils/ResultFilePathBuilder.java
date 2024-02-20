package io.soos.utils;

import io.soos.PluginConstants;

public class ResultFilePathBuilder {

    private String teamcityDataPath;
    private String systemFolder;
    private String artifactsFolder;
    private String projectDir;
    private String buildFolder;
    private String buildIdFolder;

    public ResultFilePathBuilder(String teamcityDataPath, String systemFolder, String artifactsFolder,
                                 String buildTypeIdProperty, String buildFolder, String buildIdFolder) {
        if (true) {
            buildFolder = buildFolder.replace(" ", "\\ ");
        } else {
            buildFolder = buildFolder.replace(" ", "^ ");
        }
        String[] buildTypeIdArray = buildTypeIdProperty.split("_");
        String projectDir = buildTypeIdArray[0];
        this.teamcityDataPath = teamcityDataPath;
        this.systemFolder = systemFolder;
        this.artifactsFolder = artifactsFolder;
        this.projectDir = projectDir;
        this.buildFolder = buildFolder;
        this.buildIdFolder = buildIdFolder;
    }

    public String createPath(){
        String pathSeparator = PluginConstants.SLASH;
        if ( true ){
            pathSeparator = PluginConstants.BACK_SLASH;
            teamcityDataPath = teamcityDataPath.replace(PluginConstants.C_BACK_SLASH_COLON_DOUBLE_BACK_SLASH, PluginConstants.C_COLON_BACK_SLASH);
        }
        return new StringBuilder(teamcityDataPath)
                .append(pathSeparator)
                .append(systemFolder)
                .append(pathSeparator)
                .append(artifactsFolder)
                .append(pathSeparator)
                .append(projectDir)
                .append(pathSeparator)
                .append(buildFolder)
                .append(pathSeparator)
                .append(buildIdFolder)
                .append(pathSeparator).toString();
    }
}
