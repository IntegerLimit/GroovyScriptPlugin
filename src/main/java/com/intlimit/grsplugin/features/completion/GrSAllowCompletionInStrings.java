package com.intlimit.grsplugin.features.completion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;

public class GrSAllowCompletionInStrings extends CompletionConfidence {

    @Override
    public @NotNull ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile,
                                                   int offset) {
        var type = contextElement.getNode().getElementType();
        if (type == GroovyElementTypes.STRING_SQ || type == GroovyElementTypes.STRING_DQ)
            return ThreeState.NO;

        // Leave for groovy default to handle
        return ThreeState.UNSURE;
    }
}
