package io.soos;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.soos.integration.commons.Constants;
import jetbrains.buildServer.federation.TeamCityServer;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.commons.lang3.ObjectUtils;


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
        List<String> list = new ArrayList<>();
        parameters.forEach((key, param) -> {
            list.add(param);
        });
        List<String> listParams;
        if ( !ObjectUtils.isEmpty(parameters.get(PluginConstants.REPORT_STATUS_URL)) ) {
            listParams = filterListParams(list, new String[]{ PluginConstants.DEFAULT, PluginConstants.STATUS });
            listParams.add(PluginConstants.REPORT_STATUS_URL);
        } else {
            listParams = filterListParams(list, new String[]{ PluginConstants.DEFAULT });
        }
        String stringParams = String.join(PluginConstants.DELIMITER_HYPHEN, listParams);
        StringBuilder parametersSB = new StringBuilder(PluginConstants.PARAMETERS).append(stringParams);
        return parametersSB.toString();
    }

    private List<String> filterListParams(List<String> list, String[] values) {
        for (String value : values) {
            list = list.stream().filter(param -> !param.contains(value)).collect(Collectors.toList());
        }
        return list;
    }
}