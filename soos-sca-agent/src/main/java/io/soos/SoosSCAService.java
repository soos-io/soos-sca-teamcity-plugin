package io.soos;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import io.soos.integration.domain.Mode;
import io.soos.integration.domain.analysis.AnalysisResultResponse;
import io.soos.integration.validators.OSValidator;
import org.apache.commons.lang3.StringUtils;

import io.soos.integration.commons.Constants;
import io.soos.integration.domain.SOOS;
import io.soos.integration.domain.structure.StructureResponse;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.TCStreamUtil;
import org.jetbrains.annotations.NotNull;


public class SoosSCAService extends BuildServiceAdapter {


    private final Set<File> myFilesToDelete = new HashSet<>();
    private static final Logger LOG = Logger.getLogger(SoosSCAService.class.getName());
    
    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        Map<String, String> runnerParameters = getRunnerParameters();

        Map<String, String> map = new HashMap<String, String>(populateContext(runnerParameters));
        setEnvProperties(map);

        String onFailure = getRunnerParameters().get(Constants.MAP_PARAM_ON_FAILURE_KEY);
        String reportStatusUrl = getRunnerParameters().get(PluginConstants.REPORT_STATUS_URL);
        String result = "";
        Mode mode;
        try {
            SOOS soos = new SOOS();
            soos.getContext().setScriptVersion(getVersionFromProperties());
            StructureResponse structure = null;
            AnalysisResultResponse analysisResultResponse = null;
            mode = soos.getMode();
            LOG.info("--------------------------------------------");
            switch ( mode ) {
                case RUN_AND_WAIT:
                    LOG.info(PluginConstants.RUN_AND_WAIT_MODE_SELECTED);
                    LOG.info("Run and Wait Scan");
                    LOG.info("--------------------------------------------");
                    LOG.info("Analysis request is running");
                    structure = soos.startAnalysis();
                    analysisResultResponse = soos.getResults(structure.getReportStatusUrl());
                    result= analysisResultResponse.getReportUrl();
                    LOG.info("Scan analysis finished successfully. To see the results go to: " + result);
                    break;
                case ASYNC_INIT:
                    LOG.info(PluginConstants.ASYNC_INIT_MODE_SELECTED);
                    LOG.info("Async Init Scan");
                    LOG.info("--------------------------------------------");
                    structure = soos.startAnalysis();
                    result = structure.getReportStatusUrl();
                    LOG.info("reportStatusUrl from envs: " + map.get(PluginConstants.REPORT_STATUS_URL));
                    LOG.info("Analysis request is running, access the report status using this link: " + result);
                    break;
                case ASYNC_RESULT:
                    LOG.info(PluginConstants.ASYNC_RESULT_MODE_SELECTED);
                    LOG.info("Async Result Scan");
                    LOG.info("--------------------------------------------");
                    LOG.info("Checking Scan Status from: {}"+ reportStatusUrl);
                    analysisResultResponse = soos.getResults(reportStatusUrl);
                    result = analysisResultResponse.getReportUrl();
                    LOG.info("Scan analysis finished successfully. To see the results go to: " + result);
                    break;
                default:
                    throw new Exception("Invalid SCA Mode");
            }
        } catch (Exception e) {
            LOG.severe(e.toString());
            StringBuilder errorMsg = new StringBuilder("SOOS SCA cannot be done, error: ").append(e);
            if(onFailure.equals(PluginConstants.FAIL_THE_BUILD)){
                RunBuildException exception = new RunBuildException(errorMsg.toString());
                exception.setLogStacktrace(false);
                throw exception;
            } else {
                errorMsg = new StringBuilder(PluginConstants.ECHO_COMMAND).append(" '").append(errorMsg).append("'");
                return getSimpleCommandLine(errorMsg);
            }
        }
        StringBuilder scriptContent = createScriptContent(mode, result);
        return getSimpleCommandLine(scriptContent);
    }

    private SimpleProgramCommandLine getSimpleCommandLine(StringBuilder scriptContent) throws RunBuildException {
        final String script = getCustomScript(scriptContent.toString());
        setExecutableAttribute(script);
        return new SimpleProgramCommandLine(getRunnerContext(), script, Collections.emptyList());
    }

    private void setExecutableAttribute(String script) throws RunBuildException {
        try {
            TCStreamUtil.setFileMode(new File(script), PluginConstants.FILE_MODE);
        } catch ( Throwable t ){
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Failed to set executable attribute for custom script '").append(script).append("'");
            throw new RunBuildException(errorMsg.toString(), t);
        }
    }

    private String getCustomScript(String scriptContent) throws RunBuildException {
        try {
            final File scriptFile = createScriptFile();
            FileUtil.writeFileAndReportErrors(scriptFile, scriptContent);
            myFilesToDelete.add(scriptFile);
            return scriptFile.getAbsolutePath();
        } catch ( IOException e ) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Failed to create temporary custom script in directory: ").append(getAgentTempDirectory());
            RunBuildException exception = new RunBuildException(errorMsg.toString(), e);
            exception.setLogStacktrace(false);
            throw exception;
        }
    }

    private File createScriptFile() throws IOException {

        File scriptFile;
        if( OSValidator.isWindows() ) {
             scriptFile = File.createTempFile(PluginConstants.CUSTOM_SCRIPT, PluginConstants.WIN_SCRIPT_EXT, getAgentTempDirectory());
             return scriptFile;
        }
        scriptFile = File.createTempFile(PluginConstants.CUSTOM_SCRIPT, PluginConstants.UNIX_SCRIPT_EXT, getAgentTempDirectory());
        return scriptFile;
    }

    private StringBuilder createScriptContent(Mode mode, String result) {
        StringBuilder scriptContent = new StringBuilder();
        String resultText = " Open the following url to see the report: ";
        switch ( mode ){
            case RUN_AND_WAIT:
                scriptContent.append(createReportMsg(PluginConstants.RUN_AND_WAIT_MODE_SELECTED, resultText, result));
                break;
            case ASYNC_RESULT:
                scriptContent.append(createReportMsg(PluginConstants.ASYNC_RESULT_MODE_SELECTED, resultText, result));
                break;
            default:
                resultText = " Copy the following url and use it when your select the Async result mode: ";
                scriptContent.append(createReportMsg(PluginConstants.ASYNC_INIT_MODE_SELECTED, resultText, result));
        }
        return scriptContent;
    }

    private String createReportMsg(String selectedMode, String resultText, String result) {
        StringBuilder msg = new StringBuilder();
        msg.append(PluginConstants.ECHO_COMMAND)
           .append(" ")
           .append(selectedMode)
           .append("\n")
           .append(PluginConstants.ECHO_COMMAND).append(resultText)
           .append(result);
        return msg.toString();
    }

    private Map<String, String> populateContext(Map<String, String> runnerParameters) {
        Map<String, String> map = new HashMap<String,String>();

        String dirsToExclude = addSoosDirToExclusion(runnerParameters.get(Constants.MAP_PARAM_DIRS_TO_EXCLUDE_KEY));

        map.put(Constants.PARAM_PROJECT_NAME_KEY, runnerParameters.get(Constants.MAP_PARAM_PROJECT_NAME_KEY));
        map.put(Constants.PARAM_MODE_KEY, runnerParameters.get(Constants.MAP_PARAM_MODE_KEY));
        map.put(Constants.PARAM_ON_FAILURE_KEY, runnerParameters.get(Constants.MAP_PARAM_ON_FAILURE_KEY));
        map.put(Constants.PARAM_DIRS_TO_EXCLUDE_KEY, dirsToExclude);
        map.put(Constants.PARAM_FILES_TO_EXCLUDE_KEY, runnerParameters.get(Constants.MAP_PARAM_FILES_TO_EXCLUDE_KEY));
        map.put(Constants.PARAM_WORKSPACE_DIR_KEY, runnerParameters.get(PluginConstants.WORKING_DIR));
        map.put(Constants.PARAM_CHECKOUT_DIR_KEY, runnerParameters.get(PluginConstants.CHECKOUT_DIR));
        map.put(Constants.PARAM_API_BASE_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_API_BASE_URI_KEY));
        map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, runnerParameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY));
        map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, runnerParameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY));
        map.put(Constants.PARAM_OPERATING_ENVIRONMENT_KEY, runnerParameters.get(Constants.MAP_PARAM_OPERATING_ENVIRONMENT_KEY));
        map.put(Constants.PARAM_BRANCH_NAME_KEY, runnerParameters.get(Constants.MAP_PARAM_BRANCH_NAME_KEY));
        map.put(Constants.PARAM_BRANCH_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_BRANCH_URI_KEY));
        map.put(Constants.PARAM_COMMIT_HASH_KEY, runnerParameters.get(Constants.MAP_PARAM_COMMIT_HASH_KEY));
        map.put(Constants.PARAM_BUILD_VERSION_KEY, runnerParameters.get(Constants.MAP_PARAM_BUILD_VERSION_KEY));
        map.put(Constants.PARAM_BUILD_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_BUILD_URI_KEY));
        map.put(Constants.PARAM_INTEGRATION_NAME_KEY, PluginConstants.INTEGRATION_NAME);
        map.put(Constants.SOOS_CLIENT_ID, getSystemProperties().get(Constants.SOOS_CLIENT_ID));
        map.put(Constants.SOOS_API_KEY, getSystemProperties().get(Constants.SOOS_API_KEY));
        map.put(PluginConstants.REPORT_STATUS_URL, getSystemProperties().get(PluginConstants.REPORT_STATUS_URL));

        if(StringUtils.isBlank(getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        }
        if(StringUtils.isBlank(getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        }

        return map;
    }

    private void setEnvProperties(Map<String, String> map){

        map.forEach((key, value) -> {
            if(StringUtils.isNotBlank(value)) {
                System.setProperty(key, value);
            }
        });

    }

    @Override
    public void afterProcessFinished() throws RunBuildException {
        super.afterProcessFinished();
        for( File file : myFilesToDelete ){
            FileUtil.delete(file);
        }
        myFilesToDelete.clear();
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
            LOG.severe(error.toString());
        }
        return null;
    }
}
