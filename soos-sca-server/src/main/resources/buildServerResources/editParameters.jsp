<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>


<c:set var="projectName" value="projectName"/>
<c:set var="dirsToExclude" value="dirsToExclude"/>
<c:set var="filesToExclude" value="filesToExclude"/>
<c:set var="packageManagers" value="packageManagers"/>
<c:set var="onFailure" value="onFailure"/>
<c:set var="apiURL" value="apiURL"/>
<c:set var="logLevel" value="logLevel"/>
<c:set var="verbose" value="verbose"/>
<c:set var="outputFormat" value="outputFormat"/>
<c:set var="nodePath" value="nodePath"/>


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
        <th><label for="${packageManagers}">Package Managers to look for: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${packageManagers}" size="36"/>
                <span class="error" id="error_${packageManagers}"></span>
            </div>
            <div>
                <label>Separate Package Manager names with a comma</label>
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
        <th><label for="${apiURL}">API Base URL: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${apiURL}" size="36" />
                <span class="error" id="error_${apiURL}"></span>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${logLevel}">Log Level: </label></th>
        <td>
            <div class="posRel">
                <props:selectProperty name="${logLevel}">
                    <props:option value="DEBUG">DEBUG</props:option>
                    <props:option value="INFO">INFO</props:option>
                    <props:option value="WARN">WARN</props:option>
                    <props:option value="FAIL">FAIL</props:option>
                    <props:option value="ERROR">ERROR</props:option>
                </props:selectProperty>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${verbose}">Enable Verbose Logging: </label></th>
        <td>
            <div class="posRel">
                <props:checkboxProperty name="${verbose}" />
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${outputFormat}">Output Format: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${outputFormat}" size="36" />
                <span class="error" id="error_${outputFormat}"></span>
            </div>
        </td>
    </tr>
    <tr>
        <th><label for="${nodePath}">Node Path: </label></th>
        <td>
            <div class="posRel">
                <props:textProperty name="${nodePath}" size="36" />
                <span class="error" id="error_${nodePath}"></span>
            </div>
        </td>
    </tr>



</l:settingsGroup>