package com.intlimit.grsplugin.lsp;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.redhat.devtools.lsp4ij.ServerStatus;

@Service
public final class BroadcastServerStatus implements Disposable {

    private final List<ServerStatusListener> listeners = new ArrayList<>();
    private boolean disposed = false;

    public void addListener(ServerStatusListener runnable) {
        if (isDisposed()) return;

        listeners.add(runnable);
    }

    public void removeListener(ServerStatusListener runnable) {
        if (isDisposed()) return;

        listeners.remove(runnable);
    }

    public void serverStatusChanged(ServerStatus status) {
        if (isDisposed()) return;

        listeners.forEach(consumer -> consumer.serverStatusChanged(status));
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
