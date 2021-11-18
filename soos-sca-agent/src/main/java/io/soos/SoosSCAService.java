package io.soos;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.soos.integration.domain.Mode;
import org.apache.commons.lang3.StringUtils;

import io.soos.integration.commons.Constants;
import io.soos.integration.domain.SOOS;
import io.soos.integration.domain.analysis.AnalysisResultResponse;
import io.soos.integration.domain.structure.StructureResponse;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.TCStreamUtil;


public class SoosSCAService extends BuildServiceAdapter {
    
    private final Set<File> myFilesToDelete = new HashSet<File>();
    private static Logger LOG = Logger.getLogger(SoosSCAService.class.getName());
    
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        Map<String, String> map = new HashMap<String,String>();

        Map<String, String> runnerParameters = getRunnerParameters();

        String dirsToExclude = addSoosDirToExclusion(runnerParameters.get(Constants.MAP_PARAM_DIRS_TO_EXCLUDE_KEY));

        map.put(Constants.PARAM_PROJECT_NAME_KEY, runnerParameters.get(Constants.MAP_PARAM_PROJECT_NAME_KEY));
        map.put(Constants.PARAM_MODE_KEY, runnerParameters.get(Constants.MAP_PARAM_MODE_KEY));
        map.put(Constants.PARAM_ON_FAILURE_KEY, runnerParameters.get(Constants.MAP_PARAM_ON_FAILURE_KEY));
        map.put(Constants.PARAM_DIRS_TO_EXCLUDE_KEY, dirsToExclude);
        map.put(Constants.PARAM_FILES_TO_EXCLUDE_KEY, runnerParameters.get(Constants.MAP_PARAM_FILES_TO_EXCLUDE_KEY));
        map.put(Constants.PARAM_WORKSPACE_DIR_KEY, runnerParameters.get(PluginConstants.WORKING_DIR));
        map.put(Constants.PARAM_CHECKOUT_DIR_KEY, runnerParameters.get(PluginConstants.CHECKOUT_DIR));
        map.put(Constants.PARAM_API_BASE_URI_KEY,PluginConstants.SOOS_DEFAULT_API_URL);
        map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, runnerParameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY));
        map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, runnerParameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY));
        map.put(Constants.PARAM_OPERATING_ENVIRONMENT_KEY, runnerParameters.get(Constants.MAP_PARAM_OPERATING_ENVIRONMENT_KEY));
        map.put(Constants.PARAM_BRANCH_NAME_KEY, runnerParameters.get(Constants.MAP_PARAM_BRANCH_NAME_KEY));
        map.put(Constants.PARAM_BRANCH_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_BRANCH_URI_KEY));
        map.put(Constants.PARAM_COMMIT_HASH_KEY, runnerParameters.get(Constants.MAP_PARAM_COMMIT_HASH_KEY));
        map.put(Constants.PARAM_BUILD_VERSION_KEY, runnerParameters.get(Constants.MAP_PARAM_BUILD_VERSION_KEY));
        map.put(Constants.PARAM_BUILD_URI_KEY, runnerParameters.get(Constants.MAP_PARAM_BUILD_URI_KEY));
        map.put(Constants.PARAM_INTEGRATION_NAME_KEY, PluginConstants.INTEGRATION_NAME);
        
        if(StringUtils.isBlank(getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        }
        if(StringUtils.isBlank(getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        }

        String onFailure = getRunnerParameters().get(Constants.MAP_PARAM_ON_FAILURE_KEY);

        setEnvProperties(map);
        String reportUrl = "";
        Mode mode;
        try {
            SOOS soos = new SOOS();

            StructureResponse structure = soos.getStructure();
            LOG.info(structure.toString());

            long filesProcessed = soos.sendManifestFiles(structure.getProjectId(), structure.getAnalysisId());
            LOG.info("File processed: ".concat(String.valueOf(filesProcessed)));
            mode = soos.getMode();
            if(filesProcessed > 0) {
                soos.startAnalysis(structure.getProjectId(), structure.getAnalysisId());
                AnalysisResultResponse results;
                switch (soos.getMode()) {
                    case RUN_AND_WAIT:
                        results = soos.getResults(structure.getReportStatusUrl());
                        reportUrl = soos.getStructure().getReportURL();
                        LOG.info(results.toString());
                        break;
                    case ASYNC_INIT:
                        LOG.info("async_init mode selected, starting asynchronous analysis...");
                        soos.startAnalysis(structure.getProjectId(), structure.getAnalysisId());
                        break;
                    case ASYNC_RESULT:
                        LOG.info("async_result mode selected, getting result from previous analysis...");
                        results = soos.getResults(structure.getReportStatusUrl());
                        reportUrl = soos.getStructure().getReportURL();
                        LOG.info(results.toString());
                        break;
                }

            }

        } catch (Exception e) {
            LOG.severe(e.toString());
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("SOOS SCA cannot be done, error: ").append(e.toString());

            RunBuildException exception = new RunBuildException(errorMsg.toString());
            exception.setLogStacktrace(false);
            if(onFailure.equals(PluginConstants.FAIL_THE_BUILD)){
                throw exception;
            } else {
                return new SimpleProgramCommandLine(getRunnerContext(), "/bin/echo", Arrays.asList(new String[]{exception.toString()}));
            }
        }

        StringBuilder scriptContent = new StringBuilder();
        if( !mode.equals(Mode.ASYNC_INIT)){
            scriptContent.append("/bin/echo 'CLICK ON THE LINK TO SEE THE REPORT: ").append(reportUrl).append("'");
        } else {
            scriptContent.append("/bin/echo 'async_init mode selected, starting asynchronous analysis...'");
        }

        final String script = getCustomScript(scriptContent.toString());

        setExecutableAttribute(script);

        return new SimpleProgramCommandLine(getRunnerContext(), script, Collections.emptyList());
    }

    private void setEnvProperties(Map<String, String> map){

        map.forEach((key, value) -> {
            if(StringUtils.isNotBlank(value)) {
                System.setProperty(key, value);
            }
        });

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
            final File scriptFile = File.createTempFile("custom_script", ".sh", getAgentTempDirectory());
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
    
}
