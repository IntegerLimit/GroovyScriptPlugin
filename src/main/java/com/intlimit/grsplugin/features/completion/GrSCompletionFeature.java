package com.intlimit.grsplugin.features.completion;

import java.util.Objects;

import org.eclipse.lsp4j.CompletionItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionFeature;
import com.redhat.devtools.lsp4ij.internal.StringUtils;

@SuppressWarnings("UnstableApiUsage")
public class GrSCompletionFeature extends LSPCompletionFeature {

    @Override
    public @Nullable LookupElement createLookupElement(@NotNull CompletionItem item,
                                                       @NotNull LSPCompletionContext context) {
        var type = context.getParameters().getPosition().getNode().getElementType();
        if (!StringUtils.isBlank(item.getLabel()) &&
                (type == GroovyElementTypes.STRING_SQ || type == GroovyElementTypes.STRING_DQ))
            return new GrSObjectMapperCompletionItem(item, context, this, type == GroovyElementTypes.STRING_DQ);

        return super.createLookupElement(item, context);
    }

    @Override
    public boolean isCompletionTriggerCharactersSupported(@NotNull PsiFile file, String charTyped) {
        // Don't trigger immediately after quotes, wait for alphanumeric (like) input
        // Accept ':' to trigger (resource locations)
        return !Objects.equals(charTyped, "'") && !Objects.equals(charTyped, "\"") &&
                (super.isCompletionTriggerCharactersSupported(file, charTyped) || Objects.equals(charTyped, ":"));
    }
}
