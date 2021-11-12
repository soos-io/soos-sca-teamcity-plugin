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

        String dirsToExclude = addSoosDirToExclusion(getRunnerParameters().get(Constants.MAP_PARAM_DIRS_TO_EXCLUDE_KEY));

        map.put(Constants.PARAM_PROJECT_NAME_KEY, getRunnerParameters().get(Constants.MAP_PARAM_PROJECT_NAME_KEY));
        map.put(Constants.PARAM_MODE_KEY, getRunnerParameters().get(Constants.MAP_PARAM_MODE_KEY));
        map.put(Constants.PARAM_ON_FAILURE_KEY, getRunnerParameters().get(Constants.MAP_PARAM_ON_FAILURE_KEY));
        map.put(Constants.PARAM_DIRS_TO_EXCLUDE_KEY, dirsToExclude);
        map.put(Constants.PARAM_FILES_TO_EXCLUDE_KEY, getRunnerParameters().get(Constants.MAP_PARAM_FILES_TO_EXCLUDE_KEY));
        map.put(Constants.PARAM_WORKSPACE_DIR_KEY, getRunnerParameters().get("teamcity.build.workingDir"));
        map.put(Constants.PARAM_CHECKOUT_DIR_KEY, getRunnerParameters().get("teamcity.build.checkoutDir"));
        map.put(Constants.PARAM_API_BASE_URI_KEY,"https://dev-api.soos.io/api/"); //Constants.SOOS_DEFAULT_API_URL
        map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY));
        map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY));
        map.put(Constants.PARAM_OPERATING_ENVIRONMENT_KEY, getRunnerParameters().get(Constants.MAP_PARAM_OPERATING_ENVIRONMENT_KEY)); // GETOUT
        map.put(Constants.PARAM_BRANCH_NAME_KEY, getRunnerParameters().get(Constants.MAP_PARAM_BRANCH_NAME_KEY)); 
        map.put(Constants.PARAM_BRANCH_URI_KEY, getRunnerParameters().get(Constants.MAP_PARAM_BRANCH_URI_KEY));        
        map.put(Constants.PARAM_COMMIT_HASH_KEY, getRunnerParameters().get(Constants.MAP_PARAM_COMMIT_HASH_KEY));     
        map.put(Constants.PARAM_BUILD_VERSION_KEY, getRunnerParameters().get(Constants.MAP_PARAM_BUILD_VERSION_KEY));
        map.put(Constants.PARAM_BUILD_URI_KEY, getRunnerParameters().get(Constants.MAP_PARAM_BUILD_URI_KEY));
        map.put(Constants.PARAM_INTEGRATION_NAME_KEY, PluginConstants.INTEGRATION_NAME);
        
        if(StringUtils.isBlank(getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        }
        if(StringUtils.isBlank(getRunnerParameters().get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY))) {
            map.put(Constants.PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        }

        String onFailure = getRunnerParameters().get(Constants.MAP_PARAM_ON_FAILURE_KEY);
        getRunnerParameters().forEach((k,v) -> {
            LOG.severe(k + " - " +v);
			
		});
        setEnvProperties(map);
        String reportUrl = "";
        try {
            SOOS soos = new SOOS();

            StructureResponse structure = soos.getStructure();
            System.out.println(structure.toString());

            long filesProcessed = soos.sendManifestFiles(structure.getProjectId(), structure.getAnalysisId());
            LOG.info("File processed: ".concat(String.valueOf(filesProcessed)));
            
            if(filesProcessed > 0) {
                soos.startAnalysis(structure.getProjectId(), structure.getAnalysisId());

                switch (soos.getMode()) {
                    case RUN_AND_WAIT:
                        AnalysisResultResponse results = soos.getResults(structure.getReportStatusUrl());
                        reportUrl = soos.getStructure().getReportURL();

                        LOG.info(results.toString());
                        
                        break;
                    case ASYNC_INIT:
                        break;
                    case ASYNC_RESULT:
                        break;
                }

            }

        } catch (Exception e) {
            LOG.severe(e.toString());
            RunBuildException exception = new RunBuildException("SOOS SCA cannot be done, error: ".concat(e.toString()));
            exception.setLogStacktrace(false);
            if(onFailure.equals(PluginConstants.FAIL_THE_BUILD)){
                throw exception;
            } else {
                return new SimpleProgramCommandLine(getRunnerContext(), "/bin/echo", Arrays.asList(new String[]{exception.toString()}));
            }
        }
    
        final String scriptContent = "/bin/echo 'CLICK ON THE LINK TO SEE THE REPORT: ".concat(reportUrl).concat("'");

        final String script = getCustomScript(scriptContent);

        setExecutableAttribute(script);

        return new SimpleProgramCommandLine(getRunnerContext(), script, Collections.emptyList());
    }

    private void setEnvProperties(Map<String, String> map){

        map.forEach((key, value) -> {
            if(StringUtils.isNotBlank(value)) {
                System.setProperty(key, value);
                LOG.warning(key + ": "+ value);
            }
        });

    }

    private void setExecutableAttribute(String script) throws RunBuildException {
        try {
            TCStreamUtil.setFileMode(new File(script), "a+x");
        } catch ( Throwable t ){
            throw new RunBuildException("Failed to set executable attribute for custom script '".concat(script).concat("'"), t);
        }
    }

    private String getCustomScript(String scriptContent) throws RunBuildException {
        try {
            final File scriptFile = File.createTempFile("custom_script", ".sh", getAgentTempDirectory());
            FileUtil.writeFileAndReportErrors(scriptFile, scriptContent);
            myFilesToDelete.add(scriptFile);
            return scriptFile.getAbsolutePath();
        } catch ( IOException e ) {
            RunBuildException exception = new RunBuildException("Failed to create temporary custom script in directory: ".concat(getAgentTempDirectory().toString()), e);
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