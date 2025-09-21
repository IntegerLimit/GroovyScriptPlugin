package com.intlimit.grsplugin.features.status;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intlimit.grsplugin.lsp.GrSLanguageServerFactory;
import com.intlimit.grsplugin.settings.GrSSettings;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;

public class GrSStatusBarFactory implements StatusBarWidgetFactory {

    @Override
    public @NotNull @NonNls String getId() {
        return "GrSStatusBarFactory";
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return GrSStatusBarBundle.message("status.display");
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        GrSSettings.State settings = GrSSettings.getInstance(project).getState();
        if (settings == null || !(settings.enable && settings.statusBar))
            return false;

        ServerStatus status = LanguageServerManager.getInstance(project).getServerStatus(GrSLanguageServerFactory.ID);
        return status != ServerStatus.none;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new GrSStatusBarWidget();
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }
}
