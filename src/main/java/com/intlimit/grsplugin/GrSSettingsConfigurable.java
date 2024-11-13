package com.intlimit.grsplugin;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.platform.lsp.api.LspServerManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class GrSSettingsConfigurable implements Configurable {
    private final Project project;
    private final Disposable disposable;

    private GrSSettingsComponent component;

    public GrSSettingsConfigurable(Project project) {
        this.project = project;
        this.disposable = Disposer.newDisposable("GroovyScript Configurable Disposable");
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "GroovyScript";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new GrSSettingsComponent(disposable);
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        var state = Objects.requireNonNull(GrSSettings.getInstance(project).getState());
        return component.getPort() != state.port || component.shouldDisplayTexture() != state.showTextures;
    }

    @Override
    public void apply() {
        var state = Objects.requireNonNull(GrSSettings.getInstance(project).getState());
        state.enable = component.enabled();
        state.port = component.getPort();
        state.showTextures = component.shouldDisplayTexture();

        // Restart GrS LSP Client
        if (state.enable) {
            //noinspection UnstableApiUsage
            LspServerManager.getInstance(project).stopAndRestartIfNeeded(GrSServerSupportProvider.class);
        } else {
            //noinspection UnstableApiUsage
            LspServerManager.getInstance(project).stopServers(GrSServerSupportProvider.class);
        }

    }

    @Override
    public void reset() {
        var state = Objects.requireNonNull(GrSSettings.getInstance(project).getState());
        component.setEnable(state.enable);
        component.setPort(state.port);
        component.setDisplayTexture(state.showTextures);
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return component.getPanel();
    }

    @Override
    public void disposeUIResources() {
        component = null;
        Disposer.dispose(disposable);
    }
}
