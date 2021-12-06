package io.soos;

import io.soos.integration.commons.Constants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Validation {

    private static String projectName;
    private static String analysisResultMaxWait;
    private static String analysisResultPollingInterval;
    private static String apiBaseURI;

    public Validation(){};

    public static List<InvalidProperty> validateParams(Map<String, String> properties){
        List<InvalidProperty> list = new ArrayList<>();
        projectName = properties.get(Constants.MAP_PARAM_PROJECT_NAME_KEY);
        analysisResultMaxWait = properties.get(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY);
        analysisResultPollingInterval = properties.get(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY);
        apiBaseURI = properties.get(Constants.MAP_PARAM_API_BASE_URI_KEY);

        if ( ObjectUtils.isEmpty(projectName) ) {
            list.add(new InvalidProperty(Constants.MAP_PARAM_PROJECT_NAME_KEY, ErrorMessage.SHOULD_NOT_BE_NULL));
        }

        if( !ObjectUtils.isEmpty(projectName) && projectName.length() < PluginConstants.MIN_NUMBER_OF_CHARACTERS){
            list.add(new InvalidProperty(Constants.MAP_PARAM_PROJECT_NAME_KEY, ErrorMessage.shouldBeMoreThanXCharacters(PluginConstants.MIN_NUMBER_OF_CHARACTERS)));
        }

        if( validateIsNotEmptyAndIsNumeric(analysisResultMaxWait) ){
            list.add(new InvalidProperty(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, ErrorMessage.SHOULD_BE_A_NUMBER));
        }

        if( validateIsNotEmptyAndIsNumeric(analysisResultPollingInterval) ){
            list.add(new InvalidProperty(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY , ErrorMessage.SHOULD_BE_A_NUMBER));
        }

        if( ObjectUtils.isEmpty(analysisResultMaxWait) ){
            properties.put(Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY, String.valueOf(Constants.MIN_RECOMMENDED_ANALYSIS_RESULT_MAX_WAIT));
        }
        if( ObjectUtils.isEmpty(analysisResultPollingInterval) ){
            properties.put(Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY, String.valueOf(Constants.MIN_ANALYSIS_RESULT_POLLING_INTERVAL));
        }
        if( ObjectUtils.isEmpty(apiBaseURI) ){
            properties.put(Constants.MAP_PARAM_API_BASE_URI_KEY,Constants.SOOS_DEFAULT_API_URL);
        }
        return list;
    }

    private static Boolean validateIsNotEmptyAndIsNumeric( String value ) {
        return !ObjectUtils.isEmpty(value) && !StringUtils.isNumeric(value);
    }
}
