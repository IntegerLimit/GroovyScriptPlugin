package com.intlimit.grsplugin;

import com.intellij.openapi.project.Project;
import com.intlimit.grsplugin.settings.GrSSettings;
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GrSLanguageServerFactory implements LanguageServerFactory, LanguageServerEnablementSupport {
    @Override
    public @NotNull StreamConnectionProvider createConnectionProvider(@NotNull Project project) {
        return new GrSConnectionProvider(Objects.requireNonNull(GrSSettings.getInstance(project).getState()).port);
    }

    @Override
    public boolean isEnabled(@NotNull Project project) {
        return Objects.requireNonNull(GrSSettings.getInstance(project).getState()).enable;
    }

    @Override
    public void setEnabled(boolean b, @NotNull Project project) {
        Objects.requireNonNull(GrSSettings.getInstance(project).getState()).enable = b;
    }
}
