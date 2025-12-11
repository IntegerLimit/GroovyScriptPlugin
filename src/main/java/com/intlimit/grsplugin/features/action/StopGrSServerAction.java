package com.intlimit.grsplugin.features.action;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intlimit.grsplugin.lsp.GrSLanguageServerFactory;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;

public class StopGrSServerAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        ActionUtils.updatePresentation(e, status -> status == ServerStatus.started || status == ServerStatus.starting);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            Logger.getInstance(getClass()).error("Failed to get project!");
            return;
        }

        LanguageServerManager.getInstance(project).stop(GrSLanguageServerFactory.ID);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
