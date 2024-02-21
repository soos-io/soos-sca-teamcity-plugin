package io.soos;

public final class PluginConstants {

    private PluginConstants() {}

    public static final String DISPLAY_NAME = "SOOS SCA";
    public static final String DESCRIPTION = "Scan your open source software for vulnerabilities and control the introduction of new dependencies";
    public static final String TYPE = "SOOS-SCA";
    public static final String INTEGRATION_NAME = "TeamCity";
    public static final String EDIT_PARAMETERS_FILE_NAME = "editParameters.jsp";
    public static final String WORKING_DIR = "teamcity.build.workingDir";
    public static final String CHECKOUT_DIR = "teamcity.build.checkoutDir";
    public static final Integer MIN_NUMBER_OF_CHARACTERS = 5;
    public static final String DEFAULT = "default";
    public static final String DELIMITER_HYPHEN = " - ";
    public static final String PARAMETERS = "Parameters: ";
    public static final String SLASH = "/";
    public static final String TEAMCITY_DATA_PATH_ENV = "TEAMCITY_DATA_PATH";
    public static final String TEAMCITY_BUILD_ID = "teamcity.agent.dotnet.build_id";
    public static final String TEAMCITY_BUILD_TYPE_ID = "teamcity.buildType.id";
    public static final String TEAMCITY_BUILD_CONF_NAME = "teamcity.buildConfName";

    public static final String SYSTEM_BUILD_NUMBER = "build.number";
    public static final String SYSTEM_BUILD_VCS_NUMBER = "build.vcs.number";
    public static final String OS_NAME = "os.name";
    public static final String URL = "url";
    public static final String BRANCH = "branch";

}
