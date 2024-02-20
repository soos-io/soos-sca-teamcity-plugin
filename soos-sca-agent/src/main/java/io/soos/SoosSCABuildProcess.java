package io.soos;

import io.soos.domain.TeamcityContext;
import io.soos.integration.Configuration;
import io.soos.integration.Enums;
import io.soos.integration.SoosScaWrapper;
import io.soos.utils.Utils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SoosSCABuildProcess implements BuildProcess {
    private static Configuration config = new Configuration();
    private int exitCode = 0;
    private boolean isFinished = false;
    private boolean isInterrupted = false;
    private BuildProgressLogger myBuildLogger;
    private BuildRunnerContext myContext;

    public SoosSCABuildProcess(BuildProgressLogger buildLogger, BuildRunnerContext context) {
        myBuildLogger = buildLogger;
        myContext = context;
    }

    @Override
    public void start() throws RunBuildException {
        isFinished = true;
        TeamcityContext teamcityContext = new TeamcityContext();
        teamcityContext.setDataPath(myContext.getBuildParameters().getSystemProperties().get(PluginConstants.TEAMCITY_DATA_PATH_ENV));
        teamcityContext.setBuildTypeId(myContext.getBuildParameters().getSystemProperties().get(PluginConstants.TEAMCITY_BUILD_TYPE_ID));
        teamcityContext.setBuildConfName(myContext.getBuildParameters().getSystemProperties().get(PluginConstants.TEAMCITY_BUILD_CONF_NAME));
        teamcityContext.setBuildId(myContext.getBuildParameters().getSystemProperties().get(PluginConstants.TEAMCITY_BUILD_ID));

        if ( ObjectUtils.isEmpty(teamcityContext.getDataPath()) ) {
            String teamcitySeverPath = new File(myContext.getBuildParameters().getSystemProperties().get(PluginConstants.AGENT_HOME_DIR)).getParent();
            teamcityContext.setDataPath(Utils.getTeamcityDataPath(teamcitySeverPath));
        }
        Map<String, String> runnerParameters = myContext.getRunnerParameters();
        Map<String, String> map = new HashMap<>(populateContext(runnerParameters));
        setConfigurationProperties(map, config);

        try {
            PrintStream printStream = new PrintStream(new OutputStream() {
                private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                @Override
                public void write(int b) throws IOException {
                    buffer.write(b);
                    if (b == '\n') {
                        myBuildLogger.message(buffer.toString("UTF-8"));
                        buffer.reset();
                    }
                }
            });
            SoosScaWrapper soosScaWrapper = new SoosScaWrapper(config, printStream);
            exitCode =  soosScaWrapper.runSca();

        } catch (Exception e) {
            StringBuilder errorMsg = new StringBuilder("There was an unexpected error during SOOS Sca Scan: ").append(e);
            myBuildLogger.error(errorMsg.toString());
            RunBuildException ex = new RunBuildException(errorMsg.toString(), e);
            ex.setLogStacktrace(false);
            throw ex;
        }

    }

    private void setConfigurationProperties(Map<String, String> map, Configuration configuration) {
        Class<?> configClass = configuration.getClass();
        for (Field field : configClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String value = map.get(fieldName);

            if (map.containsKey(fieldName) && StringUtils.isNotBlank(value)) {
                try {

                    if (field.getType().equals(boolean.class)) {
                        field.setBoolean(configuration, Boolean.parseBoolean(value));
                    } else {
                        field.set(configuration, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Map<String, String> populateContext(Map<String, String> runnerParameters) {
        String branchUri = "";
        String branchName = "";
        String commitHash = myContext.getBuildParameters().getSystemProperties().get(PluginConstants.SYSTEM_BUILD_VCS_NUMBER);
        String buildId = myContext.getBuildParameters().getSystemProperties().get(PluginConstants.SYSTEM_BUILD_NUMBER);
        for (VcsRootEntry entry : myContext.getBuild().getVcsRootEntries()) {
            if (entry != null && entry.getVcsRoot() != null) {
                branchUri = entry.getVcsRoot().getProperty(PluginConstants.URL);
                branchName = entry.getVcsRoot().getProperty(PluginConstants.BRANCH);

                if (branchName != null && !branchName.isEmpty()) {
                    String[] arr = branchName.split(PluginConstants.SLASH);
                    branchName = arr[arr.length - 1];
                }
            }
        }

        Map<String, String> map = new HashMap<>();

        map.put("operatingEnvironment", Utils.getOperatingSystem());
        map.put("branchName", branchName);
        map.put("branchURI", branchUri);
        map.put("commitHash", commitHash);
        map.put("buildVersion", buildId);
        map.put("workingDirectory", runnerParameters.get(PluginConstants.WORKING_DIR));
        map.put("sourceCodePath", runnerParameters.get(PluginConstants.CHECKOUT_DIR));
        map.put("integrationName", PluginConstants.INTEGRATION_NAME);
        map.put("clientId", myContext.getBuildParameters().getSystemProperties().get("SOOS_CLIENT_ID"));
        map.put("apiKey", myContext.getBuildParameters().getSystemProperties().get("SOOS_API_KEY"));
        map.putAll(runnerParameters);
        Map<String,String> configParams = myContext.getConfigParameters();

        if(!configParams.get("teamcity.build.triggeredBy.username").isBlank()) {
            map.put("contributingDeveloperId", configParams.get("teamcity.build.triggeredBy.username"));
            map.put("contributingDeveloperSourceName", "teamcity.build.triggeredBy.username");
        }else if(!configParams.get("teamcity.build.triggeredBy").isBlank()){
            map.put("contributingDeveloperId", configParams.get("teamcity.build.triggeredBy"));
            map.put("contributingDeveloperSourceName", "teamcity.build.triggeredBy");
        }

        return map;
    }


    @Override
    public boolean isInterrupted() {
        return isFinished && isInterrupted;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
    }

    @Override
    public BuildFinishedStatus waitFor() throws RunBuildException {
        if (exitCode != 0) {
            if(config.getOnFailure().equalsIgnoreCase(Enums.OnFailure.FAIL_THE_BUILD.toString())){
               return BuildFinishedStatus.FINISHED_FAILED;
            }
            return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
        }
        return BuildFinishedStatus.FINISHED_SUCCESS;
    }

}