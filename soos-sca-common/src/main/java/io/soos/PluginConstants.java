package io.soos;

public final class PluginConstants {

    private PluginConstants() {}

    public static final String DISPLAY_NAME = "SOOS SCA";
    public static final String DESCRIPTION = "Scan your open source software for vulnerabilities and control the introduction of new dependencies";
    public static final String TYPE = "SOOS-SCA";
    public static final String FAIL_THE_BUILD = "fail_the_build";
    public static final String INTEGRATION_NAME = "Teamcity";
    public static final String SOOS_DIR_NAME = "soos";
    public static final String EDIT_PARAMETERS_FILE_NAME = "editParameters.jsp";
    public static final String WORKING_DIR = "teamcity.build.workingDir";
    public static final String CHECKOUT_DIR = "teamcity.build.checkoutDir";
    public static final String FILE_MODE = "a+x";
    public static final String RUN_AND_WAIT_MODE_SELECTED = "run_and_wait mode selected, starting synchronous analysis...";
    public static final String ASYNC_INIT_MODE_SELECTED = "async_init mode selected, starting asynchronous analysis...";
    public static final String ASYNC_RESULT_MODE_SELECTED = "async_result mode selected, getting result from previous analysis...";
    public static final Integer MIN_NUMBER_OF_CHARACTERS = 5;
    public static final String PROPERTIES_FILE = "/teamcity.properties";
    public static final String VERSION = "version";
    public static final String ECHO_COMMAND = "echo";
    public static final String CUSTOM_SCRIPT = "custom_script";
    public static final String WIN_SCRIPT_EXT = ".bat";
    public static final String UNIX_SCRIPT_EXT = ".sh";
    public static final String DEFAULT = "default";
    public static final String DELIMITER_HYPHEN = " - ";
    public static final String PARAMETERS = "Parameters: ";
    public static final String SLASH = "/";
    public static final String BACK_SLASH = "\\";
    public static final String BLANK_SPACE = " ";
    public static final String LINE_BREAK = "\n";
    public static final String PIPE = " | ";
    public static final String TEE_COMMAND = "tee ";
    public static final String RESULT_FILE = "result.txt";
    public static final String TEAMCITY_DATA_PATH_ENV = "TEAMCITY_DATA_PATH";
    public static final String TEAMCITY_DATA_PATH = "teamcity.data.path";
    public static final String TEAMCITY_BUILD_ID = "teamcity.agent.dotnet.build_id";
    public static final String TEAMCITY_BUILD_TYPE_ID = "teamcity.buildType.id";
    public static final String TEAMCITY_BUILD_CONF_NAME = "teamcity.buildConfName";
    public static final String AGENT_HOME_DIR = "agent.home.dir";
    public static final String TEAMCITY_STARTUP_PROPERTIES_FILE = "teamcity-startup.properties";
    public static final String ARTIFACTS = "artifacts";
    public static final String SYSTEM = "system";
    public static final String CONFIG = "config";
    public static final String CONF = "conf";
    public static final String INTERNAL_PROPERTIES_FILE = "internal.properties";
    public static final String CONTENT_POLICY_PROPERTY = "teamcity.web.header.Content-Security-Policy.protectedValue";
    public static final String EQUAL = "=";
    public static final String IMG_SRC_POLICY = "img-src 'self' data: blob: ";
    public static final String SOOS_IMAGES_PRODUCTION_CDN = "http://cdn.mcauto-images-production.sendgrid.net/";
    public static final String GREATER_THAN = " > ";
    public static final String C_BACK_SLASH_COLON_DOUBLE_BACK_SLASH = "C\\:\\\\";
    public static final String C_COLON_BACK_SLASH = "C:\\";

    public static final String SYSTEM_BUILD_NUMBER = "build.number";
    public static final String SYSTEM_BUILD_VCS_NUMBER = "build.vcs.number";
    public static final String OS_NAME = "os.name";
    public static final String URL = "url";
    public static final String BRANCH = "branch";

}
