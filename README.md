# SOOS Analysis Integration Plugin for TeamCity

The **SOOS Analysis Integration Plugin** allows to scan your open source software for vulnerabilities and control the introduction of new dependencies, it will send and analyse your manifest files. It can be run using either synchronous or asynchronous mode.

## Supported Languages and Package Managers

*	[Node (NPM)](https://www.npmjs.com/)
*	[Python (pypi)](https://pypi.org/)
*	[.NET (NuGet)](https://www.nuget.org/)
*	[Ruby (Ruby Gems)](https://rubygems.org/)
*	[Java (Maven)](https://maven.apache.org/)

Our full list of supported manifest formats can be found [here](https://kb.soos.io/help/soos-languages-supported).

## Need an Account?
**Visit [soos.io](https://app.soos.io/register) to create your trial account.**

## Setup

Can find the plugin on [Jetbrains Marketplace](https://plugins.jetbrains.com/).

To manually install the plugin, download the zip file to 'plugins' directory under TeamCity data directory. 

| Select/Inputs | Default | Description |
| --- | --- | --- |
| Project Name | ""  | REQUIRED. A custom project name that will present itself as a collection of test results within your soos.io dashboard. |
| Mode | "run_and_wait"  | Running mode, alternatives: "async_init" - "async_result" |
| On Failure | "fail_the_build"  | Stop the building in case of failure, alternative: "continue_on_failure" |
| Operating System | "linux"  | System info regarding operating system, etc., alternatives: "win" - "mac" |
| Analysis Res. Max Wait | 300  | Maximum seconds to wait for Analysis Result before exiting with error. |
| Analysis Res. Polling Interval | 10  | Polling interval (in seconds) for analysis result completion (success/failure.). Min 10. |
| Directories To Exclude | ""  | List (comma separated) of directories (relative to ./) to exclude from the search for manifest files. Example - Correct: bin/start/ ... Example - Incorrect: ./bin/start/ ... Example - Incorrect: /bin/start/'|
| Files To Exclude | ""  | List (comma separated) of files (relative to ./) to exclude from the search for manifest files. Example - Correct: bin/start/manifest.txt ... Example - Incorrect: ./bin/start/manifest.txt ... Example - Incorrect: /bin/start/manifest.txt' |
| Commit Hash | ""  | The commit hash value from the SCM System |
| Branch Name | ""  | The name of the branch from the SCM System |
| Branch URI | ""  | The URI to the branch from the SCM System |
| Build Version | ""  | Version of application build artifacts |
| Build URI | ""  | URI to CI build info |


#### Authorization

**SOOS Analysis Integration Plugin** needs environment variables called **system properties (system.)** in teamcity which are passed as parameters. These system properties have to be declared as Parameters inside the project or the build settings, and they are required for the plugin to operate.

| Property | Description |
| --- | --- |
| system.SOOS_CLIENT_ID | Provided to you when subscribing to SOOS services. |
| system.SOOS_API_KEY | Provided to you when subscribing to SOOS services. |

These values can be found in the SOOS App under Integrate.

#### Run and wait for the analysis report
Set the **Mode** parameter to *Run and wait*, then you can run the plans in your CI/CD, and wait for the scan to complete.

#### Start the Scan
Set the **Mode** parameter to *Async init*, if you don't care about the scan result in your CI/CD plan, this is all you have to do!

#### Wait for the Scan
If you care about the result or want to break the build when issues occur, add a second task close to the end of your plan to give the scan as much time as possible to complete, setting the **Mode** parameter to *Async result*.

## Feedback and Support
### Knowledge Base
[Go To Knowledge Base](https://kb.soos.io/help)