package com.intlimit.grsplugin.settings;

import java.util.Objects;

import javax.swing.*;

import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager;
import com.intlimit.grsplugin.features.status.GrSStatusBarFactory;
import com.intlimit.grsplugin.lsp.GrSLanguageServerFactory;
import com.redhat.devtools.lsp4ij.LanguageServerManager;

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
        return component.isModified(state);
    }

    @Override
    public void apply() {
        var state = Objects.requireNonNull(GrSSettings.getInstance(project).getState());
        boolean enableChanged = state.enable != component.enabled();
        boolean portChanged = state.port != component.port();
        boolean statusBarChanged = state.statusBar != component.statusBar();

        state.enable = component.enabled();
        state.port = component.port();
        state.statusBar = component.statusBar();
        state.preTexture = component.preTexture();

        // Restart GrS LSP Client
        if (enableChanged || portChanged) {
            if (state.enable) {
                LanguageServerManager.getInstance(project).start(GrSLanguageServerFactory.ID);
            } else {
                LanguageServerManager.getInstance(project).stop(GrSLanguageServerFactory.ID);
            }
        }

        // Update Widget Visibility
        if (enableChanged || statusBarChanged) {
            var widgetFactory = StatusBarWidgetFactory.EP_NAME.findExtension(GrSStatusBarFactory.class);
            if (widgetFactory != null) {
                // noinspection IncorrectServiceRetrieving For some reason, devkit thinks this is an app level service
                project.getService(StatusBarWidgetsManager.class).updateWidget(widgetFactory);
            }
        }
    }

    @Override
    public void reset() {
        var state = Objects.requireNonNull(GrSSettings.getInstance(project).getState());
        component.setEnable(state.enable);
        component.setPort(state.port);
        component.setStatusBar(state.statusBar);
        component.setPreTexture(state.preTexture);
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return component.getPreferredFocusedComponent();
    }

    @Override
    public void disposeUIResources() {
        component = null;
        Disposer.dispose(disposable);
    }
}
