package io.soos;

import io.soos.integration.SoosScaParameters;
import jetbrains.buildServer.serverSide.InvalidProperty;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Validation {

    private static String projectName;
    private static String logLevel;
    private static Boolean verbose;
    private static String directoriesToExclude;
    private static String filesToExclude;
    private static String packageManagers;
    private static String onFailure;
    private static String apiURL;
    private static String outputFormat;
    private static String nodePath;


    public Validation() {
    }

    ;

    public static List<InvalidProperty> validateParams(Map<String, String> properties) {
        List<InvalidProperty> list = new ArrayList<>();
        projectName = properties.get(SoosScaParameters.PROJECT_NAME);
        logLevel = properties.get(SoosScaParameters.LOG_LEVEL);
        verbose = Boolean.parseBoolean(properties.get(SoosScaParameters.VERBOSE));
        directoriesToExclude = properties.get(SoosScaParameters.DIRECTORIES_TO_EXCLUDE);
        filesToExclude = properties.get(SoosScaParameters.FILES_TO_EXCLUDE);
        packageManagers = properties.get(SoosScaParameters.PACKAGE_MANAGERS);
        onFailure = properties.get(SoosScaParameters.ON_FAILURE);
        apiURL = properties.get(SoosScaParameters.API_URL);
        outputFormat = properties.get(SoosScaParameters.OUTPUT_FORMAT);
        nodePath = properties.get(SoosScaParameters.NODE_PATH);

        if (ObjectUtils.isEmpty(projectName)) {
            list.add(new InvalidProperty(SoosScaParameters.PROJECT_NAME, ErrorMessage.SHOULD_NOT_BE_NULL));
        } else if (projectName.length() < PluginConstants.MIN_NUMBER_OF_CHARACTERS) {
            list.add(new InvalidProperty(SoosScaParameters.PROJECT_NAME, ErrorMessage.shouldBeMoreThanXCharacters(PluginConstants.MIN_NUMBER_OF_CHARACTERS)));
        }

        return list;
    }
}
