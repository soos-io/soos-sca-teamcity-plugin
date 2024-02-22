package io.soos;

import jetbrains.buildServer.serverSide.InvalidProperty;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Validation {

    private static String projectName;
    private static String logLevel;
    private static Boolean verbose;
    private static String dirsToExclude;
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
        projectName = properties.get("projectName");
        logLevel = properties.get("logLevel");
        verbose = Boolean.parseBoolean(properties.get("verbose"));
        dirsToExclude = properties.get("dirsToExclude");
        filesToExclude = properties.get("filesToExclude");
        packageManagers = properties.get("packageManagers");
        onFailure = properties.get("onFailure");
        apiURL = properties.get("apiURL");
        outputFormat = properties.get("outputFormat");
        nodePath = properties.get("nodePath");

        if (ObjectUtils.isEmpty(projectName)) {
            list.add(new InvalidProperty("projectName", ErrorMessage.SHOULD_NOT_BE_NULL));
        } else if (projectName.length() < PluginConstants.MIN_NUMBER_OF_CHARACTERS) {
            list.add(new InvalidProperty("projectName", ErrorMessage.shouldBeMoreThanXCharacters(PluginConstants.MIN_NUMBER_OF_CHARACTERS)));
        }

        return list;
    }
}
