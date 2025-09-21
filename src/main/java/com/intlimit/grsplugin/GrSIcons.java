package com.intlimit.grsplugin;

import javax.swing.*;

import com.intellij.openapi.util.IconLoader;

public interface GrSIcons {

    Icon StatusNormal = IconLoader.getIcon("/icons/statusNormal.svg", GrSIcons.class);
    Icon StatusError = IconLoader.getIcon("/icons/statusError.svg", GrSIcons.class);
    Icon StatusLoading = IconLoader.getIcon("/icons/statusLoading.svg", GrSIcons.class);
}
