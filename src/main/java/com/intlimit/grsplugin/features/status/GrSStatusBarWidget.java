package com.intlimit.grsplugin.features.status;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Consumer;
import com.intlimit.grsplugin.GrSIcons;
import com.intlimit.grsplugin.lsp.BroadcastServerStatus;
import com.intlimit.grsplugin.lsp.GrSLanguageServerFactory;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;

public class GrSStatusBarWidget implements StatusBarWidget, StatusBarWidget.IconPresentation {

    private ServerStatus status;
    private StatusBar statusBar;
    private BroadcastServerStatus.ServerStatusListener listener;

    public static final String ID = "GrSStatusBarWidget";

    public GrSStatusBarWidget() {
        status = ServerStatus.starting;
    }

    @Override
    public @Nullable WidgetPresentation getPresentation() {
        return this;
    }

    @Override
    public @NotNull String ID() {
        return ID;
    }

    @Override
    public @Nullable Icon getIcon() {
        return status == ServerStatus.started ? GrSIcons.StatusNormal :
                (status == ServerStatus.starting || status == ServerStatus.stopping ? GrSIcons.StatusLoading :
                        GrSIcons.StatusError);
    }

    @Override
    public @Nullable String getTooltipText() {
        return GrSStatusBarBundle.message("status.tooltip." + status.name());
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        this.statusBar = statusBar;

        var project = statusBar.getProject();
        if (project == null) return;

        listener = this::updateStatus;
        project.getService(BroadcastServerStatus.class).addListener(listener);

        // Update status
        updateStatus(LanguageServerManager.getInstance(project).getServerStatus(GrSLanguageServerFactory.ID));
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        // Inspired by IntelliJ's default impl
        return event -> {
            var dataContext = DataManager.getInstance().getDataContext((Component) statusBar);

            // noinspection UnstableApiUsage
            var group = ActionUtil.getActionGroup("com.intlimit.grsplugin.features.action.Group");
            if (group == null) return;

            var popup = JBPopupFactory.getInstance().createActionGroupPopup(
                    GrSStatusBarBundle.message("status.display"),
                    group,
                    dataContext,
                    JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                    false);

            var dimension = popup.getContent().getPreferredSize();
            var at = new Point(0, -dimension.height);
            popup.show(new RelativePoint(event.getComponent(), at));

            // destroy popup on unexpected project close
            Disposer.register(this, popup);
        };
    }

    private void updateStatus(ServerStatus status) {
        this.status = status;

        if (statusBar == null) return;
        var project = statusBar.getProject();

        if (project != null) {
            WindowManager.getInstance().getStatusBar(project).updateWidget(GrSStatusBarWidget.ID);
        }
    }

    @Override
    public void dispose() {
        StatusBarWidget.super.dispose();

        var project = statusBar.getProject();
        if (listener != null && project != null) {
            project.getService(BroadcastServerStatus.class).removeListener(listener);
        }
    }
}
