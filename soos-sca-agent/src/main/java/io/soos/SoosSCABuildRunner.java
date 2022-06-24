package io.soos;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentBuildRunner;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildRunnerContext;

public class SoosSCABuildRunner implements AgentBuildRunner {

    @Override
    public BuildProcess createBuildProcess(AgentRunningBuild runningBuild, BuildRunnerContext context) throws RunBuildException {
        return new SoosSCABuildProcess(runningBuild.getBuildLogger(), context);
    }

    @Override
    public AgentBuildRunnerInfo getRunnerInfo() {
        return new AgentBuildRunnerInfo() {

            @Override
            public String getType() {
                return PluginConstants.TYPE;
            }

            @Override
            public boolean canRun(BuildAgentConfiguration agentConfiguration) {
                return true;
            }
        };
    }

}