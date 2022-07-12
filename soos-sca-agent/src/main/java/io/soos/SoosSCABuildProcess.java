package io.soos;

import io.soos.domain.TeamcityContext;
import io.soos.integration.commons.Constants;
import io.soos.integration.domain.Mode;
import io.soos.integration.domain.SOOS;
import io.soos.integration.domain.analysis.AnalysisResultResponse;
import io.soos.integration.domain.scan.ScanResponse;
import io.soos.utils.Utils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SoosSCABuildProcess implements BuildProcess {

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
        setEnvProperties(map);
        String onFailure = myContext.getRunnerParameters().get(Constants.MAP_PARAM_ON_FAILURE_KEY);
        String result;
        String scanStatus = "";
        Mode mode;
        try {
            SOOS soos = new SOOS();
            soos.getContext().setScriptVersion(getVersionFromProperties());
            ScanResponse scan;
            AnalysisResultResponse analysisResultResponse;
            mode = soos.getMode();
            myBuildLogger.message("--------------------------------------------");
            switch ( mode ) {
                case RUN_AND_WAIT:
                    myBuildLogger.message(PluginConstants.RUN_AND_WAIT_MODE_SELECTED);
                    myBuildLogger.message("Run and Wait Scan");
                    myBuildLogger.message("--------------------------------------------");
                    myBuildLogger.message("Analysis request is running");
                    scan = soos.startAnalysis();
                    analysisResultResponse = soos.getResults(scan.getScanStatusUrl());
                    result = analysisResultResponse.getScanUrl();
                    scanStatus = analysisResultResponse.getStatus();
                    myBuildLogger.message("Scan analysis finished successfully. To see the full report go to: " + result);
                    myBuildLogger.message("Violations found: " + analysisResultResponse.getViolations() + " | Vulnerabilities found: " + analysisResultResponse.getVulnerabilities() );
                    break;
                case ASYNC_INIT:
                    myBuildLogger.message(PluginConstants.ASYNC_INIT_MODE_SELECTED);
                    myBuildLogger.message("Async Init Scan");
                    myBuildLogger.message("--------------------------------------------");
                    scan = soos.startAnalysis();
                    result = scan.getScanStatusUrl();
                    myBuildLogger.message("Analysis request is running, access the report status using this link: " + result);
                    break;
                case ASYNC_RESULT:
                    String reportStatusUrl = Utils.getReportStatusUrl(teamcityContext);
                    myBuildLogger.message(PluginConstants.ASYNC_RESULT_MODE_SELECTED);
                    myBuildLogger.message("Async Result Scan");
                    myBuildLogger.message("--------------------------------------------");
                    myBuildLogger.message("Checking Scan Status from: ".concat(reportStatusUrl));
                    analysisResultResponse = soos.getResults(reportStatusUrl);
                    result = analysisResultResponse.getScanUrl();
                    scanStatus = analysisResultResponse.getStatus();
                    myBuildLogger.message("Scan analysis finished successfully. To see the full report go to: ".concat(result));
                    myBuildLogger.message("Violations found: " + analysisResultResponse.getViolations() + " | Vulnerabilities found: " + analysisResultResponse.getVulnerabilities() );
                    break;
                default:
                    throw new Exception("Invalid SCA Mode");
            }
        } catch (Exception e) {
            StringBuilder errorMsg = new StringBuilder("SOOS SCA cannot be done, error: ").append(e);
            RunBuildException exception = new RunBuildException(errorMsg.toString());
            exception.setLogStacktrace(false);
            throw exception;
        }

        if(scanStatus.equals(PluginConstants.FAILED_WITH_ISSUES) && onFailure.equals(PluginConstants.FAIL_THE_BUILD)) {
            RunBuildException exception = new RunBuildException("Scan failed due to vulnerabilities/policy violations");
            exception.setLogStacktrace(false);
            throw exception;
        }

    }

    private void setEnvProperties(Map<String, String> map){
        map.forEach((key, value) -> {
            if(StringUtils.isNotBlank(value)) {
                System.setProperty(key, value);
            }
        });
    }

    private Map<String, String> populateContext(Map<String, String> runnerParameters) {
        String branchUri = "";
        String branchName = "";
        String commitHash = myContext.getBuildParameters().getSystemProperties().get(PluginConstants.SYSTEM_BUILD_VCS_NUMBER);
        String buildId = myContext.getBuildParameters().getSystemProperties().get(PluginConstants.SYSTEM_BUILD_NUMBER);

        for (VcsRootEntry entry: myContext.getBuild().getVcsRootEntries()) {
            branchUri = entry.getVcsRoot().getProperty(PluginConstants.URL);
            branchName = entry.getVcsRoot().getProperty(PluginConstants.BRANCH);
        }

        String[] arr = branchName.split(PluginConstants.SLASH);
        branchName = arr[arr.length - 1];

        Map<String, String> map = new HashMap<>();
        String dirsToExclude = addSoosDirToExclusion(runnerParameters.get(Constants.MAP_PARAM_DIRS_TO_EXCLUDE_KEY));
        map.put(Constants.PARAM_PROJECT_NAME_KEY, runnerParameters.get(Constants.MAP_PARAM_PROJECT_NAME_KEY));
        map.put(Constants.PARAM_MODE_KEY, runnerParameters.get(Constants.MAP_PARAM_MODE_KEY));
        map.put(Constants.PARAM_ON_FAILURE_KEY, runnerParameters.get(Constants.MAP_PARAM_ON_FAILURE_KEY));
        map.put(Constants.PARAM_DIRS_TO_EXCLUDE_KEY, dirsToExclude);
        map.put(Constants.PARAM_PACKAGE_MANAGERS_KEY, runnerParameters.get(Constants.MAP_PARAM_PACKAGE_MANAGERS_KEY));
        map.put(Constants.PARAM_FILES_TO_EXCLUDE_KEY, runnerParameters.get(Constants.MAP_PARAM_FILES_TO_EXCLUDE_KEY));
        map.put(Constants.PARAM_WORKSPACE_DIR_KEY, runnerParameters.get(PluginConstants.WORKING_DIR));
        map.put(Constants.PARAM_CHECKOUT_DIR_KEY, runnerParameters.get(PluginConstants.CHECKOUT_DIR));
        map.put(Constants.PARAM_API_BASE_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_API_BASE_URI_KEY));
        map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, runnerParameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY));
        map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, runnerParameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY));
        map.put(Constants.PARAM_OPERATING_ENVIRONMENT_KEY, Utils.getOperatingSystem());
        map.put(Constants.PARAM_BRANCH_NAME_KEY, branchName);
        map.put(Constants.PARAM_BRANCH_URI_KEY, branchUri);
        map.put(Constants.PARAM_COMMIT_HASH_KEY, commitHash);
        map.put(Constants.PARAM_BUILD_VERSION_KEY, buildId);
        map.put(Constants.PARAM_BUILD_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_BUILD_URI_KEY));
        map.put(Constants.PARAM_INTEGRATION_NAME_KEY, PluginConstants.INTEGRATION_NAME);
        map.put(Constants.SOOS_CLIENT_ID, myContext.getBuildParameters().getSystemProperties().get(Constants.SOOS_CLIENT_ID));
        map.put(Constants.SOOS_API_KEY, myContext.getBuildParameters().getSystemProperties().get(Constants.SOOS_API_KEY));
        if(StringUtils.isBlank(myContext.getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        }
        if(StringUtils.isBlank(myContext.getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        }
        return map;
    }

    private String addSoosDirToExclusion(String dirs){
        if(StringUtils.isNotBlank(dirs)){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(dirs).append(",").append(PluginConstants.SOOS_DIR_NAME);
            return stringBuilder.toString();
        }

        return PluginConstants.SOOS_DIR_NAME;
    }

    private String getVersionFromProperties(){
        Properties prop = new Properties();
        try {
            prop.load(this.getClass().getResourceAsStream(PluginConstants.PROPERTIES_FILE));
            return prop.getProperty(PluginConstants.VERSION);
        } catch (IOException e) {
            StringBuilder error = new StringBuilder("Cannot read file ").append("'").append(PluginConstants.PROPERTIES_FILE).append("' - ").append(e);
            myBuildLogger.error(error.toString());
        }
        return null;
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
        return BuildFinishedStatus.FINISHED_SUCCESS;
    }

}