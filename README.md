# [SOOS Core SCA TeamCity Plugin](https://soos.io/sca-product)

<img src="assets/SOOS_logo.png" style="margin-bottom: 10px;" width="350" alt="SOOS Icon">

SOOS is an independent software security company, located in Winooski, VT USA, building security software for your team. [SOOS, Software security, simplified](https://soos.io).

Use SOOS to scan your software for [vulnerabilities](https://app.soos.io/research/vulnerabilities) and [open source license](https://app.soos.io/research/licenses) issues with [SOOS Core SCA](https://soos.io/sca-product). [Generate SBOMs](https://kb.soos.io/help/generating-a-software-bill-of-materials-sbom). Govern your open source dependencies. Run the [SOOS DAST vulnerability scanner](https://soos.io/dast-product) against your web apps or APIs.

[Demo SOOS](https://app.soos.io/demo) or [Register for a Free Trial](https://app.soos.io/register).

If you maintain an Open Source project, sign up for the Free as in Beer [SOOS Community Edition](https://soos.io/products/community-edition).

## How to Use

The **SOOS SCA Plugin** will locate and analyze any supported manifest files under the specified directory.

To use SOOS SCA Plugin you need to:

1. [Install the SOOS SCA Plugin](#install-the-soos-sca-plugin)
2. [Configure authorization](#configure-authorization)
3. [Configure other plugin parameters](#configure-other-plugin-parameters)

## Supported Languages and Package Managers

*	C++ (Conan)
*   [Node (NPM)](https://www.npmjs.com/)
*	[Python (pypi)](https://pypi.org/)
*	[.NET (NuGet)](https://www.nuget.org/)
*	[Ruby (Ruby Gems)](https://rubygems.org/)
*	[Java (Maven)](https://maven.apache.org/)

Our full list of supported manifest formats can be found [here](https://kb.soos.io/help/soos-languages-supported).

## Need an Account?
**Visit [soos.io](https://app.soos.io/register) to create your trial account.**

## Setup

### Install the SOOS SCA Plugin

Install or upgrade the SOOS SCA Plugin from Jetbrains Marketplace with these steps. Once complete, you’re all set to add a SOOS SCA step to your projects.

Log in to your TeamCity instance to install the SOOS SCA Plugin. Configure the Plugins list to Periodically check for plugin updates, in order to ensure regular automatic upgrades in the background. Navigate to the JetBrains Plugins Repository, search for **SOOS SCA** and from the Get dropdown list, select to install the plugin for your TeamCity installation. When the following prompt appears, click Install. When the installation ends, the Administration Plugins List loads, notifying the plugin has been uploaded. Ensure the plugin is enabled.

<blockquote style="margin-bottom: 10px;">
<details>
<summary> Show example </summary>

<img src="assets/prompt-image-to-show.png" style="margin-top: 10px; margin-bottom: 10px;" alt="Prompt-image-to-show">

</details>
</blockquote>

To manually install the plugin you have two options:

1.  Download and copy the .zip file into <teamcity-home>/plugins/. Restart the teamcity server. 
2.  Download the .zip file, log in to your TeamCity instance, go to Administration > Plugins, and click the "Upload plugin zip" button, choose the **soos-sca-plugin.zip** file, when the following prompt appears, click Upload plugin zip. Ensure the plugin is enabled.

<blockquote style="margin-bottom: 10px;">
<details>
<summary> Show example </summary>

<img src="assets/upload-plugin-zip-example.png" style="margin-top: 10px; margin-bottom: 10px;" alt="Upload Plugin Zip Example">

</details>
</blockquote>

### Configure authorization

**SOOS SCA Plugin** needs environment variables called **system properties (system.)** in teamcity which are passed as parameters. These system properties have to be declared as Parameters inside the project or the build settings, and they are required for the plugin to operate.

| Property | Description |
| --- | --- |
| system.SOOS_CLIENT_ID | Provided to you when subscribing to SOOS services. |
| system.SOOS_API_KEY | Provided to you when subscribing to SOOS services. |

These values can be found in the SOOS App under Integrate.

### Configure other plugin parameters

<blockquote style="margin-bottom: 10px;">
<details>
<summary> Show parameters </summary>

| Select/Inputs | Default | Description |
| --- | --- | --- |
| Project Name | ""  | REQUIRED. A custom project name that will present itself as a collection of test results within your soos.io dashboard. |
| Build URI | ""  | URI to CI build info |
| Directories To Exclude | ""  | List (comma separated) of directories (relative to ./) to exclude from the search for manifest files. Example - Correct: bin/start/ ... Example - Incorrect: ./bin/start/ ... Example - Incorrect: /bin/start/'|
| Files To Exclude | ""  | List (comma separated) of files (relative to ./) to exclude from the search for manifest files. Example - Correct: bin/start/manifest.txt ... Example - Incorrect: ./bin/start/manifest.txt ... Example - Incorrect: /bin/start/manifest.txt' |
| On Failure | "Fail the build"  | Stop the building in case of failure, alternative: "Continue on failure" |
| Analysis Res. Max Wait | 300  | Maximum seconds to wait for Analysis Result before exiting with error. |
| Analysis Res. Polling Interval | 10  | Polling interval (in seconds) for analysis result completion (success/failure.). Min 10. |
| API Base URL | "https://api.soos.io/api/"  | The API BASE URI provided to you when subscribing to SOOS services. |

</details>
</blockquote>


## Feedback and Support
### Knowledge Base
[Go To Knowledge Base](https://kb.soos.io/help)

