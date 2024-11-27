package com.intlimit.grsplugin.features;

import static com.redhat.devtools.lsp4ij.internal.CompletableFutures.isDoneNormally;
import static com.redhat.devtools.lsp4ij.internal.CompletableFutures.waitUntilDone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyLanguage;

import com.intellij.codeInsight.hints.*;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.psi.PsiFile;
import com.intlimit.grsplugin.server.GetTextureParams;
import com.intlimit.grsplugin.server.GetTextureResponse;
import com.redhat.devtools.lsp4ij.LSPIJUtils;
import com.redhat.devtools.lsp4ij.features.AbstractLSPInlayHintsProvider;

@SuppressWarnings("UnstableApiUsage")
public class TextureHintsProvider extends AbstractLSPInlayHintsProvider {

    private static final Logger log = Logger.getInstance(TextureHintsProvider.class);

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
        return language.isKindOf(GroovyLanguage.INSTANCE);
    }

    @Override
    protected void doCollect(@NotNull PsiFile psiFile, @NotNull Editor editor,
                             @NotNull PresentationFactory presentationFactory, @NotNull InlayHintsSink inlayHintsSink,
                             @NotNull List<CompletableFuture> pendingFutures) {
        var virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null) return;

        var params = new GetTextureParams();
        params.setTextDocument(LSPIJUtils.toTextDocumentIdentifier(virtualFile));

        CompletableFuture<List<GetTextureResponse>> future = GrSTextureHintsSupport.getSupport(psiFile)
                .getTextureHints(params);

        try {
            waitUntilDone(future);

            if (!isDoneNormally(future)) return;

            var result = future.getNow(Collections.emptyList());
            var doc = editor.getDocument();

            for (var res : result) {
                var offset = LSPIJUtils.toOffset(res.getRange().getStart(), doc);
                var presentation = new TextureElementPresentation(editor, res.getTextureUri(), res.getTooltips());
                if (presentation.invalid()) continue;
                inlayHintsSink.addInlineElement(offset, false,
                        presentation,
                        false);
            }
        } catch (ProcessCanceledException | CancellationException ignore) {} catch (ExecutionException e) {
            log.error("Error whilst consuming LSP 'groovyScript/textureDecoration' request", e);
        } finally {
            if (!future.isDone()) {
                pendingFutures.add(future);
            }
        }
    }
}
