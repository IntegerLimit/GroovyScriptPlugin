package com.intlimit.grsplugin.settings;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import com.intellij.DynamicBundle;

public class GrSSettingsBundle {

    @NonNls
    private static final String BUNDLE = "messages.Settings";
    private static final DynamicBundle INSTANCE = new DynamicBundle(GrSSettingsBundle.class, BUNDLE);

    public static @NotNull @Nls String message(
                                               @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                               Object @NotNull... params) {
        return INSTANCE.getMessage(key, params);
    }
}
