package io.soos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface PluginConstants {
    String DISPLAY_NAME = "SOOS SCA";
    String DESCRIPTION = "Scan your open source software for vulnerabilities and control the introduction of new dependencies";
    String TYPE = "SOOS-SCA";
    String FAIL_THE_BUILD = "fail_the_build";
    String INTEGRATION_NAME = "Teamcity";
    String SOOS_DIR_NAME = "soos";
    String EDIT_PARAMETERS_FILE_NAME = "editParameters.jsp";
    String WORKING_DIR = "teamcity.build.workingDir";
    String CHECKOUT_DIR = "teamcity.build.checkoutDir";
    String FILE_MODE = "a+x";
    String RUN_AND_WAIT_MODE_SELECTED = "run_and_wait mode selected, starting synchronous analysis...";
    String ASYNC_INIT_MODE_SELECTED = "async_init mode selected, starting asynchronous analysis...";
    String ASYNC_RESULT_MODE_SELECTED = "async_result mode selected, getting result from previous analysis...";
    Integer MIN_NUMBER_OF_CHARACTERS = 5;
    String PROPERTIES_FILE = "/teamcity.properties";
    String VERSION = "version";
    String ECHO_COMMAND = "echo";
    /*String UNIX_ECHO_COMMAND = "/bin/echo";*/
    String CUSTOM_SCRIPT = "custom_script";
    String WIN_SCRIPT_EXT = ".bat";
    String UNIX_SCRIPT_EXT = ".sh";
}
