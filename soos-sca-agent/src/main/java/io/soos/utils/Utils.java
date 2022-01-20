package io.soos.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import io.soos.PluginConstants;
import io.soos.domain.TeamcityContext;
import io.soos.integration.domain.Mode;
import io.soos.integration.validators.OSValidator;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.TCStreamUtil;

public class Utils {

    private static final Set<File> myFilesToDelete = new HashSet<>();

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    public static void addContentPolicyToShowSoosImgs(TeamcityContext teamcityContext) {
        String pathSeparator = PluginConstants.SLASH;
        String teamcityDataPath = teamcityContext.getDataPath();
        if ( OSValidator.isWindows() ){
            pathSeparator = PluginConstants.BACK_SLASH;
            teamcityDataPath = teamcityDataPath.replace(PluginConstants.C_BACK_SLASH_COLON_DOUBLE_BACK_SLASH, PluginConstants.C_COLON_BACK_SLASH);
        }
        StringBuilder internalPropertiesFilePath = new StringBuilder();
        internalPropertiesFilePath.append(teamcityDataPath);
        internalPropertiesFilePath.append(pathSeparator);
        internalPropertiesFilePath.append(PluginConstants.CONFIG);
        internalPropertiesFilePath.append(pathSeparator);
        internalPropertiesFilePath.append(pathSeparator);
        internalPropertiesFilePath.append(PluginConstants.INTERNAL_PROPERTIES_FILE);
        File internalPropertiesFile = new File(internalPropertiesFilePath.toString());
        try {
            internalPropertiesFile.createNewFile();
            boolean lineEdited = false;
            StringBuffer stringBuffer = new StringBuffer();
            Scanner reader = new Scanner(internalPropertiesFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String firstChar = !line.isBlank() ? Character.toString(line.trim().charAt(0)) : "";
                if ( !firstChar.equals("#") && line.contains(PluginConstants.CONTENT_POLICY_PROPERTY)
                        && line.contains(PluginConstants.IMG_SRC_POLICY)
                        && line.contains(PluginConstants.SOOS_IMAGES_PRODUCTION_CDN)) {
                    reader.close();
                    return;
                } else if ( !firstChar.equals("#") && line.contains(PluginConstants.CONTENT_POLICY_PROPERTY)
                        && line.contains(PluginConstants.IMG_SRC_POLICY) ) {
                    int index = line.indexOf("blob: ");
                    line = line.substring(0, index + 6).concat(PluginConstants.SOOS_IMAGES_PRODUCTION_CDN).concat(" ").concat(line.substring(index + 6));
                    lineEdited = true;
                }
                stringBuffer.append(line.concat(System.lineSeparator()));
            }
            reader.close();
            if ( !lineEdited ) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(internalPropertiesFile,true));
                StringBuilder fileText = new StringBuilder();
                fileText.append(PluginConstants.CONTENT_POLICY_PROPERTY);
                fileText.append(PluginConstants.EQUAL);
                fileText.append(PluginConstants.IMG_SRC_POLICY);
                fileText.append(PluginConstants.SOOS_IMAGES_PRODUCTION_CDN);
                writer.newLine();
                writer.write(fileText.toString());
                writer.close();
                return;
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(internalPropertiesFile));
            writer.write(stringBuffer.toString());
            writer.close();
        } catch (IOException e) {
            LOG.severe("Failed to write to internal.properties file: ".concat(e.getMessage()));
        }
    }

    public static String getTeamcityDataPath(String teamcityServerPath) {
        String pathSeparator = PluginConstants.SLASH;
        if ( OSValidator.isWindows() ){
            pathSeparator = PluginConstants.BACK_SLASH;
            teamcityServerPath = teamcityServerPath.replace(PluginConstants.C_BACK_SLASH_COLON_DOUBLE_BACK_SLASH, PluginConstants.C_COLON_BACK_SLASH);
        }
        StringBuilder teamcityStartupPropertiesPath = new StringBuilder(teamcityServerPath);
        teamcityStartupPropertiesPath.append(pathSeparator);
        teamcityStartupPropertiesPath.append(PluginConstants.CONF);
        teamcityStartupPropertiesPath.append(pathSeparator);
        teamcityStartupPropertiesPath.append(PluginConstants.TEAMCITY_STARTUP_PROPERTIES_FILE);
        File teamcityStartupPropertiesFile = new File(teamcityStartupPropertiesPath.toString());
        Scanner reader;
        try {
            reader = new Scanner(teamcityStartupPropertiesFile);
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

    public static void setExecutableAttribute(String script) throws RunBuildException {
        try {
            TCStreamUtil.setFileMode(new File(script), PluginConstants.FILE_MODE);
        } catch ( Throwable t ){
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Failed to set executable attribute for custom script '").append(script).append("'");
            throw new RunBuildException(errorMsg.toString(), t);
        }
    }

    public static String getCustomScript(String scriptContent, TeamcityContext teamcityContext) throws RunBuildException {
        try {
            final File scriptFile = createScriptFile(teamcityContext);
            FileUtil.writeFileAndReportErrors(scriptFile, scriptContent);
            myFilesToDelete.add(scriptFile);
            return scriptFile.getAbsolutePath();
        } catch ( IOException e ) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Failed to create temporary custom script in directory: ").append(teamcityContext.getAgentTempDirectory());
            RunBuildException exception = new RunBuildException(errorMsg.toString(), e);
            exception.setLogStacktrace(false);
            throw exception;
        }
    }

    public static File createScriptFile(TeamcityContext teamcityContext) throws IOException {

        File scriptFile;
        if( OSValidator.isWindows() ) {
            scriptFile = File.createTempFile(PluginConstants.CUSTOM_SCRIPT, PluginConstants.WIN_SCRIPT_EXT, teamcityContext.getAgentTempDirectory());
            return scriptFile;
        }
        scriptFile = File.createTempFile(PluginConstants.CUSTOM_SCRIPT, PluginConstants.UNIX_SCRIPT_EXT, teamcityContext.getAgentTempDirectory());
        return scriptFile;
    }

    public static StringBuilder createScriptContent(Mode mode, String result, TeamcityContext teamcityContext) {
        StringBuilder scriptContent = new StringBuilder();
        String resultText = " Click to see report: ";
        switch ( mode ){
            case RUN_AND_WAIT:
                scriptContent.append(createReportMsg(PluginConstants.RUN_AND_WAIT_MODE_SELECTED, resultText, result));
                break;
            case ASYNC_RESULT:
                scriptContent.append(createReportMsg(PluginConstants.ASYNC_RESULT_MODE_SELECTED, resultText, result));
                break;
            default:
                resultText = " Copy the following url and use it when your select the Async result mode: ";
                scriptContent.append(createReportMsg(PluginConstants.ASYNC_INIT_MODE_SELECTED, resultText, result));
        }

        String artifact_path = getBuildArtifactsDirectory(teamcityContext);
        if ( !OSValidator.isWindows() ) {
            scriptContent.append(PluginConstants.PIPE);
            scriptContent.append(PluginConstants.TEE_COMMAND);
        } else {
            scriptContent.append(PluginConstants.GREATER_THAN);
        }
        scriptContent.append(artifact_path);
        scriptContent.append(PluginConstants.RESULT_FILE);
        return scriptContent;
    }

    public static String getBuildArtifactsDirectory(TeamcityContext teamcityContext){
        ResultFilePathBuilder resultFilePathBuilder = new ResultFilePathBuilder(teamcityContext.getDataPath(), PluginConstants.SYSTEM,
                PluginConstants.ARTIFACTS, teamcityContext.getBuildTypeId(), teamcityContext.getBuildConfName(), teamcityContext.getBuildId());
        return resultFilePathBuilder.createPath();
    }

    private static String createReportMsg(String selectedMode, String resultText, String result) {
        StringBuilder msg = new StringBuilder();
        msg.append(PluginConstants.ECHO_COMMAND)
                .append(" ")
                .append(selectedMode)
                .append("\n")
                .append(PluginConstants.ECHO_COMMAND).append(resultText)
                .append(result);
        return msg.toString();
    }

    public static void removeTempScripts() {
        for( File file : myFilesToDelete ){
            FileUtil.delete(file);
        }
        myFilesToDelete.clear();
    }
}
