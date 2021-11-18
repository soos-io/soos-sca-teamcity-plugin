package io.soos;

public interface PluginConstants {
    String DISPLAY_NAME = "SOOS SCA";
    String DESCRIPTION = "Scan your open source software for vulnerabilities and control the introduction of new dependencies";
    String TYPE = "soosSCAType";
    String FAIL_THE_BUILD = "fail_the_build";
    String INTEGRATION_NAME = "Teamcity";
    String SOOS_DIR_NAME = "soos";
    String EDIT_PARAMETERS_FILE_NAME = "editParameters.jsp";
    String SOOS_DEFAULT_API_URL = "https://dev-api.soos.io/api/";
    String WORKING_DIR = "teamcity.build.workingDir";
    String CHECKOUT_DIR = "teamcity.build.checkoutDir";
    String FILE_MODE = "a+x";


}
