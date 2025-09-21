package com.intlimit.grsplugin.features.completion;

import org.eclipse.lsp4j.CompletionItem;
import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.util.TextRange;
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionFeature;
import com.redhat.devtools.lsp4ij.client.features.LSPCompletionProposal;

@SuppressWarnings("UnstableApiUsage")
public class GrSObjectMapperCompletionItem extends LSPCompletionProposal {

    private final int completionOffset;
    private final boolean doubleQuoted;

    public GrSObjectMapperCompletionItem(@NotNull CompletionItem item,
                                         LSPCompletionFeature.@NotNull LSPCompletionContext context,
                                         @NotNull LSPCompletionFeature completionFeature,
                                         boolean doubleQuoted) {
        super(item, context, completionFeature);
        this.completionOffset = context.getParameters().getOffset();
        this.doubleQuoted = doubleQuoted;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        // Unsure why we need WriteAction, shouldn't according to docs
        // Could be due to LSP requiring completed futures?
        WriteAction.run(() -> {
            // Find the start of selection, properly
            // (Previous Impl Incl Quotes)
            // Go to position after quotes, but go reverse from completion trigger
            // (so we don't find another element's quotes)
            char toFind = doubleQuoted ? '"' : '\'';
            char[] searchText = context.getDocument().getText(new TextRange(context.getStartOffset(), completionOffset))
                    .toCharArray();

            int i;
            for (i = searchText.length - 1; i >= 0; i--) {
                if (searchText[i] == toFind) break;
            }

            // Start after quotes
            int startOffset = context.getStartOffset() + i + 1;

            context.getDocument().replaceString(startOffset, context.getTailOffset(), getLookupString());
            context.getEditor().getCaretModel().moveToOffset(startOffset + getLookupString().length());
            context.commitDocument();
        });
    }
}
