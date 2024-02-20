package io.soos.utils;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import io.soos.PluginConstants;
import io.soos.domain.TeamcityContext;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    public static String getTeamcityDataPath(String teamcityServerPath) {
        String pathSeparator = PluginConstants.SLASH;
        if ( true ){
            pathSeparator = PluginConstants.BACK_SLASH;
            teamcityServerPath = teamcityServerPath.replace(PluginConstants.C_BACK_SLASH_COLON_DOUBLE_BACK_SLASH, PluginConstants.C_COLON_BACK_SLASH);
        }
        StringBuilder teamcityStartupPropertiesPath = new StringBuilder(teamcityServerPath);
        teamcityStartupPropertiesPath.append(pathSeparator);
        teamcityStartupPropertiesPath.append(PluginConstants.CONF);
        teamcityStartupPropertiesPath.append(pathSeparator);
        teamcityStartupPropertiesPath.append(PluginConstants.TEAMCITY_STARTUP_PROPERTIES_FILE);
        File teamcityStartupPropertiesFile = new File(teamcityStartupPropertiesPath.toString());
        try {
            Scanner reader = new Scanner(teamcityStartupPropertiesFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if ( line.contains(PluginConstants.TEAMCITY_DATA_PATH) ) {
                    String[] array = line.split(PluginConstants.EQUAL);
                    reader.close();
                    return array[1];
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBuildArtifactsDirectory(TeamcityContext teamcityContext){
        ResultFilePathBuilder resultFilePathBuilder = new ResultFilePathBuilder(teamcityContext.getDataPath(), PluginConstants.SYSTEM,
                PluginConstants.ARTIFACTS, teamcityContext.getBuildTypeId(), teamcityContext.getBuildConfName(), teamcityContext.getBuildId());
        return resultFilePathBuilder.createPath();
    }

    public static String getOperatingSystem() {
        return System.getProperty(PluginConstants.OS_NAME).toLowerCase();
    }

    public static String getReportStatusUrl(TeamcityContext teamcityContext) throws Exception {
        StringBuilder actualPath = new StringBuilder(getBuildArtifactsDirectory(teamcityContext));
        File buildsFolder = new File(actualPath.toString()).getParentFile();
        File previousBuildFolder = getPreviousBuildFolder(buildsFolder);
        StringBuilder resultFilePath;

        if ( previousBuildFolder != null) {
            resultFilePath = new StringBuilder(previousBuildFolder.getAbsolutePath());
        } else {
            throw new Exception("Cannot find the status url from the previous build");
        }

        LOG.info(resultFilePath.toString());
        File file = new File(resultFilePath.toString());
        String resultStatusUrl = "";
        try {
            Scanner scanner = new Scanner(file);
            String data = scanner.nextLine();
            String[] arr = data.split(": ");
            resultStatusUrl = arr[1].trim();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultStatusUrl;
    }

    public static File getPreviousBuildFolder(final File path) {
        List<File> folders = Arrays.asList(path.listFiles());
        Collections.sort(folders, (f1, f2) -> {
            if (f1.isDirectory() && f2.isDirectory()) {
                return Long.compare(f1.lastModified(),f2.lastModified());
            }
            return -1;
        });
        for (int i = folders.size() -1; i >= 0; i--) {
            File file = folders.get(i);
            if( file.isDirectory() && !file.getName().equals(".teamcity") ) {
                File foundFile = getPreviousBuildFolder(file);
                if (foundFile != null) {
                    return foundFile;
                }
            } else if (file.getName().equals(PluginConstants.RESULT_FILE)){
                try {
                    Scanner scanner = new Scanner(file);
                    String data = scanner.nextLine();
                    if ( data.contains("Report status URL") ) {
                        scanner.close();
                        return file;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
