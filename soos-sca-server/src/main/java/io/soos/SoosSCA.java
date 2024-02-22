package io.soos;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;


public class SoosSCA extends RunType {

    private PluginDescriptor descriptor;

    public SoosSCA(RunTypeRegistry registry,
                   PluginDescriptor descriptor) {
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
        List<String> listParams = filterListParams(list);
        String stringParams = String.join(PluginConstants.DELIMITER_HYPHEN, listParams);
        StringBuilder parametersSB = new StringBuilder(PluginConstants.PARAMETERS).append(stringParams);
        return parametersSB.toString();
    }

    private List<String> filterListParams(List<String> list) {
        return list.stream().filter(param -> !param.contains(PluginConstants.DEFAULT)).collect(Collectors.toList());
    }
}