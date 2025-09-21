package com.intlimit.grsplugin.features.completion;

import java.util.Objects;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.redhat.devtools.lsp4ij.LSPIJUtils;
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionFeature;
import com.redhat.devtools.lsp4ij.internal.StringUtils;

@SuppressWarnings("UnstableApiUsage")
public class GrSCompletionFeature extends LSPCompletionFeature {

    @Override
    public @Nullable LookupElement createLookupElement(@NotNull CompletionItem item,
                                                       @NotNull LSPCompletionContext context) {
        var type = context.getParameters().getPosition().getNode().getElementType();
        if (!StringUtils.isBlank(item.getLabel()) &&
                (type == GroovyElementTypes.STRING_SQ || type == GroovyElementTypes.STRING_DQ)) {
            // Find the start of selection, properly
            // (Previous Impl Incl Quotes)
            // Go to position after quotes, but go reverse from completion trigger
            // (so we don't find another element's quotes)
            char toFind = type == GroovyElementTypes.STRING_DQ ? '"' : '\'';
            Document document = context.getParameters().getEditor().getDocument();
            int startOffset = context.getParameters().getPosition().getTextOffset();
            int endOffset = context.getParameters().getOffset();
            char[] searchText = document.getText(new TextRange(startOffset, endOffset)).toCharArray();

            int i;
            for (i = searchText.length - 1; i >= 0; i--) {
                if (searchText[i] == toFind) break;
            }

            // Start after quotes
            startOffset += i + 1;

            // Include quotes in the replacement, so we move the cursor to after quotes
            endOffset += 1;

            item.setTextEdit(Either.forLeft(new TextEdit(
                    new Range(LSPIJUtils.toPosition(startOffset, document), LSPIJUtils.toPosition(endOffset, document)),
                    item.getLabel() + "'")));
        }

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
