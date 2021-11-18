package io.soos;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import io.soos.integration.commons.Constants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;


public class SoosSCA extends RunType {
    
    private PluginDescriptor descriptor;    
    
    public SoosSCA(RunTypeRegistry registry,
                        PluginDescriptor descriptor){
        this.descriptor = descriptor;
        registry.registerRunType(this);
    }
    
    @Override
    public String getDescription() {
        return PluginConstants.DESCRIPTION;
    }

    @Override
    public String getDisplayName() {
        return PluginConstants.DISPLAY_NAME;
    }

    @Override
    public String getType() {      
        return PluginConstants.TYPE;
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return new HashMap<>();
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return properties -> {

            describeParameters(properties);
            List<InvalidProperty> list = new ArrayList<>();
            final String projectName = properties.get(Constants.MAP_PARAM_PROJECT_NAME_KEY);
            final String analysisResultMaxWait = properties.get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY);
            final String analysisResultPollingInterval = properties.get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY);

            if ( ObjectUtils.isEmpty(projectName) ) {
                list.add(new InvalidProperty(Constants.MAP_PARAM_PROJECT_NAME_KEY, ErrorMessage.SHOULD_NOT_BE_NULL));
            }
            
            if( !ObjectUtils.isEmpty(projectName) && projectName.length() < 5){
                list.add(new InvalidProperty(Constants.MAP_PARAM_PROJECT_NAME_KEY, ErrorMessage.SHOULD_BE_MORE_THAN_5_CHARACTERS));
            }

            if( !ObjectUtils.isEmpty(analysisResultMaxWait) && !validateNumber(analysisResultMaxWait)){
                list.add(new InvalidProperty(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, ErrorMessage.SHOULD_BE_A_NUMBER));
            }
            
            if( !ObjectUtils.isEmpty(analysisResultPollingInterval) && !validateNumber(analysisResultPollingInterval)){
                list.add(new InvalidProperty(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY , ErrorMessage.SHOULD_BE_A_NUMBER));
            }
        

            return list;
        };
        
    }
    @Override
    public String getEditRunnerParamsJspFilePath() {
       return descriptor.getPluginResourcesPath(PluginConstants.EDIT_PARAMETERS_FILE_NAME);
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return null;
    }

    @Override
    public String describeParameters(Map<String, String> parameters) {

        List<String> listParams = new ArrayList<>();
        
        parameters.forEach((key, param) -> {
            listParams.add(param);
        });

        final String resultMaxWait = parameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY);
        final String resultPollingInterval = parameters.get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY);
    
        if(ObjectUtils.isEmpty(resultMaxWait)){
            listParams.add(String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        }
        if(ObjectUtils.isEmpty(resultPollingInterval)){
            listParams.add(String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        }
    
        String stringParams = String.join(" - ", listParams.stream().filter(param -> !param.equals("default")).collect(Collectors.toList()));
        return "Parameters: ".concat(stringParams);
    }

    private Boolean validateNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch ( Exception e ){
            return false;
        }
    }
}