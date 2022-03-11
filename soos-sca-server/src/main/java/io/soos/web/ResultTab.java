package io.soos.web;

import io.soos.PluginConstants;
import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.web.*;
import jetbrains.buildServer.web.openapi.BuildTab;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class ResultTab extends BuildTab {

    private static final Logger LOG = Logger.getLogger(ResultTab.class.getName());

    protected ResultTab(WebControllerManager manager, BuildsManager buildManager, PluginDescriptor descriptor) {
        super("soosResultTab", "SOOS Result", manager, buildManager,
                descriptor.getPluginResourcesPath("soosReport.jsp"));
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> model, @NotNull SBuild build) {

        StringBuilder path = new StringBuilder(build.getArtifactsDirectory().getAbsolutePath());
        path.append(PluginConstants.SLASH);
        path.append(PluginConstants.RESULT_FILE);

        File file = new File(path.toString());
        try {
            Scanner scanner = new Scanner(file);
            String data = scanner.nextLine();
            String[] arr = data.split(": ");
            model.put("url", arr[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
