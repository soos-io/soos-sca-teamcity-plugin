package io.soos.web;

import com.intellij.openapi.util.io.StreamUtil;
import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactHolder;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.BuildTab;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

public class ResultTab extends BuildTab {

    protected ResultTab(WebControllerManager manager, BuildsManager buildManager, PluginDescriptor descriptor) {
        super("soosResultTab", "SOOS Result", manager, buildManager,
                descriptor.getPluginResourcesPath("soosReport.jsp"));
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> model, @NotNull SBuild build) {
        final BuildArtifacts buildArtifacts = build.getArtifacts(BuildArtifactsViewMode.VIEW_DEFAULT);
        final BuildArtifactHolder artifact = buildArtifacts.findArtifact("result.txt");

        if ( artifact.isAvailable() ) {
            try {
                final String text = StreamUtil.readText(artifact.getArtifact().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
