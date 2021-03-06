/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfrog.idea.ui.xray;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jfrog.idea.configuration.GlobalSettings;
import org.jfrog.idea.xray.scan.ScanManager;
import org.jfrog.idea.xray.ScanManagerFactory;

public class XrayToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull final ToolWindow toolWindow) {
        boolean supported = isMavenProject(project);
        DumbService.getInstance(project).runWhenSmart(() -> ServiceManager.getService(project, XrayToolWindow.class).initToolWindow(toolWindow, supported));

        ScanManager scanManager = ScanManagerFactory.getScanManager(project);
        if (supported && GlobalSettings.getInstance().isCredentialsSet()) {
            scanManager.asyncScanAndUpdateResults(true);
        }
    }

    public boolean isMavenProject(Project project) {
        return !MavenProjectsManager.getInstance(project).getProjects().isEmpty();
    }
}
