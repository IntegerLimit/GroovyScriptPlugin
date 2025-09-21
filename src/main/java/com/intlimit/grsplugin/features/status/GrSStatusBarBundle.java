package com.intlimit.grsplugin.features.status;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import com.intellij.DynamicBundle;
import com.intlimit.grsplugin.settings.GrSSettingsBundle;

public class GrSStatusBarBundle {

    @NonNls
    private static final String BUNDLE = "messages.Status";
    private static final DynamicBundle INSTANCE = new DynamicBundle(GrSSettingsBundle.class, BUNDLE);

    public static @NotNull @Nls String message(
                                               @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                               Object @NotNull... params) {
        return INSTANCE.getMessage(key, params);
    }
}
