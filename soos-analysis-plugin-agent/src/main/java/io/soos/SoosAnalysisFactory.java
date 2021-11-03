package io.soos;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;

public class SoosAnalysisFactory implements CommandLineBuildServiceFactory{

    @Override
    public CommandLineBuildService createService() {
        return new SoosAnalysisService();
        
    }

    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return new AgentBuildRunnerInfo() {

            @Override
            public boolean canRun(BuildAgentConfiguration arg0) {
                return true;
            }

            @Override
            public String getType() {
                return PluginConstants.TYPE;
            }
            
        };
    }
}
