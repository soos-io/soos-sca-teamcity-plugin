package io.soos.utils;

import io.soos.PluginConstants;

public class Utils {

    public static String getOperatingSystem() {
        return System.getProperty(PluginConstants.OS_NAME).toLowerCase();
    }

}
