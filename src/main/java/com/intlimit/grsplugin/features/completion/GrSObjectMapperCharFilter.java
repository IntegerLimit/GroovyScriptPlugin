package com.intlimit.grsplugin.features.completion;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Allow colons (':') to continue completions.
 */
public class GrSObjectMapperCharFilter extends CharFilter {

    @Override
    public @Nullable Result acceptChar(char c, int prefixLength, Lookup lookup) {
        PsiFile file = lookup.getPsiFile();
        PsiElement element = lookup.getPsiElement();

        if (file == null || file.getLanguage() != GroovyLanguage.INSTANCE || element == null) return null;

        var type = element.getNode().getElementType();
        if (c == ':' && type == GroovyElementTypes.STRING_SQ || type == GroovyElementTypes.STRING_DQ)
            return Result.ADD_TO_PREFIX;

        return null;
    }
}
