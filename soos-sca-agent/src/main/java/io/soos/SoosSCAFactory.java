package io.soos;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;

public class SoosSCAFactory implements CommandLineBuildServiceFactory{

    @Override
    public CommandLineBuildService createService() {
        return new SoosSCAService();
        
    }

    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return new AgentBuildRunnerInfo() {

            @Override
            public boolean canRun( BuildAgentConfiguration buildAgentConfiguration ) {
                return true;
            }

            @Override
            public String getType() {
                return PluginConstants.TYPE;
            }
            
        };
    }
}
