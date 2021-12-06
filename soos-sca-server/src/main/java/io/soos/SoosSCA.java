package io.soos;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
        Map<String, String> map = new HashMap<>();

        map.put(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        map.put(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        map.put(Constants.MAP_PARAM_API_BASE_URI_KEY, Constants.SOOS_DEFAULT_API_URL);

        return map;
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return properties -> {
            List<InvalidProperty> list = Validation.validateParams(properties);
            describeParameters(properties);
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

        String stringParams = String.join(" - ", listParams.stream().filter(param -> !param.equals("default")).collect(Collectors.toList()));
        return "Parameters: ".concat(stringParams);
    }
}