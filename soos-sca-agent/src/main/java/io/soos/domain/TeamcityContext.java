package io.soos.domain;


import java.io.File;

public class TeamcityContext {

    private String dataPath;
    private String buildTypeId;
    private String buildConfName;
    private String buildId;
    private File agentTempDirectory;

    public TeamcityContext() {
    }

    public TeamcityContext(String dataPath, String buildTypeId, String buildConfName, String buildId, File agentTempDirectory) {
        this.dataPath = dataPath;
        this.buildTypeId = buildTypeId;
        this.buildConfName = buildConfName;
        this.buildId = buildId;
        this.agentTempDirectory = agentTempDirectory;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getBuildTypeId() {
        return buildTypeId;
    }

    public void setBuildTypeId(String buildTypeId) {
        this.buildTypeId = buildTypeId;
    }

    public String getBuildConfName() {
        return buildConfName;
    }

    public void setBuildConfName(String buildConfName) {
        this.buildConfName = buildConfName;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public File getAgentTempDirectory() {
        return agentTempDirectory;
    }

    public void setAgentTempDirectory(File agentTempDirectory) {
        this.agentTempDirectory = agentTempDirectory;
    }
}
