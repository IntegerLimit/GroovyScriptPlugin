package com.intlimit.grsplugin.features;

import com.intellij.codeInsight.hints.*;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyLanguage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TextureHintsProvider implements InlayHintsProvider<NoSettings> {

    private final SettingsKey<NoSettings> key = new SettingsKey<>("GroovyScript.TextureHintsProvider");

    @Override
    public @NotNull NoSettings createSettings() {
        return new NoSettings();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getName() {
        return "GroovyScript texture";
    }

    @Override
    public @NotNull SettingsKey<NoSettings> getKey() {
        return key;
    }

    @Override
    public @Nullable String getPreviewText() {
        return "Preview";
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull NoSettings noSettings) {
        return new ImmediateConfigurable() {
            @Override
            public @NotNull JComponent createComponent(@NotNull ChangeListener listener) {
                return new JPanel();
            }

            @Override
            public @NotNull String getMainCheckboxText() {
                return "Show hints for:";
            }

            @Override
            public @NotNull List<Case> getCases() {
                return new ArrayList<>();
            }
        };
    }

    @Override
    public boolean isLanguageSupported(@NotNull Language language) {
        return language.is(GroovyLanguage.INSTANCE);
    }

    @Override
    public @Nullable InlayHintsCollector getCollectorFor(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull NoSettings noSettings, @NotNull InlayHintsSink inlayHintsSink) {
        return new TextureHintsCollector(editor, psiFile);
    }
}
