<%@ page import="io.soos.integration.commons.Constants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<c:set var="projectName" value="<%=Constants.MAP_PARAM_PROJECT_NAME_KEY%>"/>
<c:set var="commitHash" value="<%=Constants.MAP_PARAM_COMMIT_HASH_KEY%>"/>
<c:set var="branchURI" value="<%=Constants.MAP_PARAM_BRANCH_URI_KEY%>"/>
<c:set var="branchName" value="<%=Constants.MAP_PARAM_BRANCH_NAME_KEY%>"/>
<c:set var="buildVersion" value="<%=Constants.MAP_PARAM_BUILD_VERSION_KEY%>"/>
<c:set var="buildURI" value="<%=Constants.MAP_PARAM_BUILD_URI_KEY%>"/>
<c:set var="mode" value="<%=Constants.MAP_PARAM_MODE_KEY%>"/>
<c:set var="dirsToExclude" value="<%=Constants.MAP_PARAM_DIRS_TO_EXCLUDE_KEY%>"/>
<c:set var="filesToExclude" value="<%=Constants.MAP_PARAM_FILES_TO_EXCLUDE_KEY%>"/>
<c:set var="packageManagers" value="<%=Constants.MAP_PARAM_PACKAGE_MANAGERS_KEY%>"/>

<c:set var="onFailure" value="<%=Constants.MAP_PARAM_ON_FAILURE_KEY%>"/>
<c:set var="analysisResultMaxWait" value="<%=Constants.MAP_PARAM_ANALYSIS_RESULT_MAX_WAIT_KEY%>"/>
<c:set var="resultPollingInterval" value="<%=Constants.MAP_PARAM_ANALYSIS_RESULT_POLLING_INTERVAL_KEY%>"/>
<c:set var="apiBaseURI" value="<%=Constants.MAP_PARAM_API_BASE_URI_KEY%>"/>

<script type="text/javascript">
    function sendModeAndCleanField(mode) {
        if ( mode !== 'async_result') {
            document.getElementById("reportStatusURL").value = '';
        }
    }
</script>

<l:settingsGroup title="SOOS SCA settings">
    <tr>
        <th><label for="${projectName}">Project Name: <l:star/></label></th>
        <td>
            <div class="posRel">
                <props:textProperty id="projectName" name="${projectName}" size="36"/>
                <span class="error" id="error_${projectName}"></span>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${buildURI}">Build URI: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${buildURI}" size="36" />
                <span class="error" id="error_${buildURI}"></span>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${mode}">Mode: </label></th>
        <td>
            <div class="posRel">
                <props:selectProperty name="${mode}" onchange="sendModeAndCleanField(this.value)">
                    <props:option value="run_and_wait">Run and wait</props:option>
                    <props:option value="async_init">Async init</props:option>
                    <props:option value="async_result">Async result</props:option>
                </props:selectProperty>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${dirsToExclude}">Directories To Exclude: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${dirsToExclude}" size="36" />
                <span class="error" id="error_${dirsToExclude}"></span>
            </div>
            <div>
                <label>Separate directory names with a comma</label>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${filesToExclude}">Files To Exclude: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${filesToExclude}" size="36" />
                <span class="error" id="error_${filesToExclude}"></span>
            </div>
            <div>
                <label>Separate file names with a comma</label>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${onFailure}">On Failure: </label></th>
        <td>
            <div class="posRel">
                <props:selectProperty name="${onFailure}">
                    <props:option value="continue_on_failure">Continue on failure</props:option>
                    <props:option value="fail_the_build">Fail the build</props:option>
                </props:selectProperty>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${packageManagers}">Package Managers to look for: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${packageManagers}" size="36" />
                <span class="error" id="error_${packageManagers}"></span>
            </div>
            <div>
                <label>Separate Package Managers names with a comma</label>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${analysisResultMaxWait}">Analysis Res. Max Wait: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${analysisResultMaxWait}" size="36" />
                <span class="error" id="error_${analysisResultMaxWait}"></span>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${resultPollingInterval}">Analysis Res. Polling Interval: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${resultPollingInterval}" size="36" />
                <span class="error" id="error_${resultPollingInterval}"></span>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${apiBaseURI}">API Base URL: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${apiBaseURI}" size="36" />
                <span class="error" id="error_${apiBaseURI}"></span>
            </div>
        </td>
    </tr>

</l:settingsGroup>