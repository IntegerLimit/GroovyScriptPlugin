package com.intlimit.grsplugin.features.completion;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import com.intlimit.grsplugin.settings.GrSSettings;

public class GrSAllowCompletionInStrings extends CompletionConfidence {

    @Override
    public @NotNull ThreeState shouldSkipAutopopup(@NotNull Editor editor, @NotNull PsiElement contextElement,
                                                   @NotNull PsiFile psiFile, int offset) {
        var project = editor.getProject();
        if (project == null)
            return ThreeState.UNSURE;

        boolean lspEnabled = Objects.requireNonNull(GrSSettings.getInstance(project).getState()).enable &&
                GrSSettings.getInstance(project).currEnable();
        if (!lspEnabled)
            return ThreeState.UNSURE;

        var type = contextElement.getNode().getElementType();
        if (type == GroovyElementTypes.STRING_SQ || type == GroovyElementTypes.STRING_DQ)
            return ThreeState.NO; // Turn on completion

        return ThreeState.UNSURE;
    }
}
