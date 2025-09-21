package com.intlimit.grsplugin.lsp;

import java.util.Objects;

import org.eclipse.lsp4j.services.LanguageServer;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intlimit.grsplugin.settings.GrSSettings;
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;

public class GrSLanguageServerFactory implements LanguageServerFactory, LanguageServerEnablementSupport {

    public static final String ID = "groovyscript";

    private boolean enableNow = true;

    @Override
    public @NotNull StreamConnectionProvider createConnectionProvider(@NotNull Project project) {
        return new GrSConnectionProvider(Objects.requireNonNull(GrSSettings.getInstance(project).getState()).port);
    }

    @Override
    public @NotNull Class<? extends LanguageServer> getServerInterface() {
        return GrSServerAPI.class;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @NotNull LSPClientFeatures createClientFeatures() {
        return new GrSClientFeatures();
    }

    @Override
    public boolean isEnabled(@NotNull Project project) {
        return Objects.requireNonNull(GrSSettings.getInstance(project).getState()).enable && enableNow;
    }

    @Override
    public void setEnabled(boolean b, @NotNull Project project) {
        enableNow = b;
    }
}
