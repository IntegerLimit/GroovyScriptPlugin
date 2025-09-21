package com.intlimit.grsplugin.lsp;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager;
import com.intlimit.grsplugin.features.status.GrSStatusBarFactory;
import com.redhat.devtools.lsp4ij.ServerStatus;

@Service(Service.Level.PROJECT)
public final class BroadcastServerStatus implements Disposable {

    private final List<ServerStatusListener> listeners = new ArrayList<>();
    private ServerStatus prevStatus = ServerStatus.none;
    private boolean disposed = false;

    public void addListener(ServerStatusListener runnable) {
        if (isDisposed()) return;

        listeners.add(runnable);
    }

    public void removeListener(ServerStatusListener runnable) {
        if (isDisposed()) return;

        listeners.remove(runnable);
    }

    public void serverStatusChanged(ServerStatus status, Project project) {
        if (isDisposed()) return;

        listeners.forEach(consumer -> consumer.serverStatusChanged(status));

        if (prevStatus == ServerStatus.none || status == ServerStatus.none) {
            var widgetFactory = StatusBarWidgetFactory.EP_NAME.findExtension(GrSStatusBarFactory.class);
            if (widgetFactory != null) {
                // noinspection IncorrectServiceRetrieving For some reason, devkit thinks this is an app level service
                project.getService(StatusBarWidgetsManager.class).updateWidget(widgetFactory);
            }
        }

        prevStatus = status;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void dispose() {
        disposed = true;
        listeners.clear();
    }

    @FunctionalInterface
    public interface ServerStatusListener {

        void serverStatusChanged(ServerStatus status);
    }
}
