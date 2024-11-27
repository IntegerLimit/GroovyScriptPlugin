package com.intlimit.grsplugin.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;

@Service(Service.Level.PROJECT)
@State(
       name = "org.intellij.sdk.settings.AppSettings",
       storages = @Storage("GroovyScriptSettings.xml"))
public final class GrSSettings implements PersistentStateComponent<GrSSettings.State> {

    public static int DEFAULT_PORT = 25564;

    public static class State {

        public boolean enable = true;
        public int port = DEFAULT_PORT;
    }

    private State state = new State();

    public static GrSSettings getInstance(Project project) {
        return project.getService(GrSSettings.class);
    }

    @Override
    public @Nullable State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
