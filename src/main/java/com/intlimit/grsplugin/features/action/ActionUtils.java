package com.intlimit.grsplugin.features.action;

import java.util.function.Predicate;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intlimit.grsplugin.lsp.GrSLanguageServerFactory;
import com.intlimit.grsplugin.settings.GrSSettings;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;

public class ActionUtils {

    public static void updatePresentation(AnActionEvent e, Predicate<ServerStatus> validServerStatus) {
        var project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        var settings = GrSSettings.getInstance(project).getState();
        if (settings == null || !settings.enable) {
            e.getPresentation().setEnabled(false);
            return;
        }

        var status = LanguageServerManager.getInstance(project).getServerStatus(GrSLanguageServerFactory.ID);
        e.getPresentation().setEnabled(validServerStatus.test(status));
    }
}
