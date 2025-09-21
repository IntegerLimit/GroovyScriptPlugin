package com.intlimit.grsplugin.lsp;

import org.jetbrains.annotations.NotNull;

import com.intlimit.grsplugin.features.completion.GrSCompletionFeature;
import com.redhat.devtools.lsp4ij.ServerStatus;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;

@SuppressWarnings("UnstableApiUsage")
public class GrSClientFeatures extends LSPClientFeatures {

    public GrSClientFeatures() {
        setCompletionFeature(new GrSCompletionFeature());
    }

    @Override
    public void handleServerStatusChanged(@NotNull ServerStatus status) {
        getProject().getService(BroadcastServerStatus.class).serverStatusChanged(status, getProject());
    }
}
