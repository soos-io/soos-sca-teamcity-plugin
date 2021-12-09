package io.soos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface PluginConstants {
    public String DISPLAY_NAME = "SOOS SCA";
    public String DESCRIPTION = "Scan your open source software for vulnerabilities and control the introduction of new dependencies";
    public String TYPE = "SOOS-SCA";
    public String FAIL_THE_BUILD = "fail_the_build";
    public String INTEGRATION_NAME = "Teamcity";
    public String SOOS_DIR_NAME = "soos";
    public String EDIT_PARAMETERS_FILE_NAME = "editParameters.jsp";
    public String WORKING_DIR = "teamcity.build.workingDir";
    public String CHECKOUT_DIR = "teamcity.build.checkoutDir";
    public String FILE_MODE = "a+x";
    public String RUN_AND_WAIT_MODE_SELECTED = "run_and_wait mode selected, starting synchronous analysis...";
    public String ASYNC_INIT_MODE_SELECTED = "async_init mode selected, starting asynchronous analysis...";
    public String ASYNC_RESULT_MODE_SELECTED = "async_result mode selected, getting result from previous analysis...";
    public Integer MIN_NUMBER_OF_CHARACTERS = 5;
}
