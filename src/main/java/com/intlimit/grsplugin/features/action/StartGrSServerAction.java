package com.intlimit.grsplugin.features.action;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intlimit.grsplugin.lsp.GrSLanguageServerFactory;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;

public class StartGrSServerAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        ActionUtils.updatePresentation(e, status -> status == ServerStatus.stopped);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getRequiredData(CommonDataKeys.PROJECT);
        var manager = LanguageServerManager.getInstance(project);

        manager.start(GrSLanguageServerFactory.ID);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
