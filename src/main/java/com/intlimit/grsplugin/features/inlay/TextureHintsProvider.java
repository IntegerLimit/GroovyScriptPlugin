package com.intlimit.grsplugin.features.inlay;

import static com.redhat.devtools.lsp4ij.internal.CompletableFutures.isDoneNormally;
import static com.redhat.devtools.lsp4ij.internal.CompletableFutures.waitUntilDone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.lsp4j.TextDocumentIdentifier;
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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intlimit.grsplugin.lsp.GetTextureParams;
import com.intlimit.grsplugin.lsp.GetTextureResponse;
import com.intlimit.grsplugin.lsp.GrSServerAPI;
import com.intlimit.grsplugin.settings.GrSSettings;
import com.redhat.devtools.lsp4ij.LSPIJUtils;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.ServerStatus;
import com.redhat.devtools.lsp4ij.features.AbstractLSPInlayHintsProvider;

@SuppressWarnings("UnstableApiUsage")
public class TextureHintsProvider extends AbstractLSPInlayHintsProvider {

    private static final Logger log = Logger.getInstance(TextureHintsProvider.class);

    private final SettingsKey<NoSettings> key = new SettingsKey<>("GroovyScript.TextureHintsProvider");
    private static final AtomicReference<String> previousFile = new AtomicReference<>("");
    private static final AtomicLong previousSavedModStamp = new AtomicLong(-1);
    private static volatile CompletableFuture<List<Pair<GetTextureResponse, PsiElement>>> previousResults = CompletableFuture
            .completedFuture(Collections.emptyList());

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
                             @NotNull PresentationFactory presentationFactory,
                             @NotNull InlayHintsSink inlayHintsSink) {
        GrSSettings.State settings = GrSSettings.getInstance(psiFile.getProject()).getState();
        if (settings == null || !settings.enable || settings.preTexture)
            return;

        var virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null) return;

        String path = virtualFile.getPath();
        long modStamp = psiFile.getModificationStamp();

        if (!path.equals(previousFile.get()) || modStamp != previousSavedModStamp.get())
            handleNewRequest(editor, psiFile, virtualFile, path, modStamp);

        if (waitForResults(psiFile)) return;

        addResultsToSink(editor, inlayHintsSink);
    }

    /**
     * Returns true if exceptional completion, false if success.
     */
    private boolean waitForResults(PsiFile file) {
        // noinspection IncorrectCancellationExceptionHandling
        try {
            waitUntilDone(previousResults, file);

            if (!isDoneNormally(previousResults)) return true;
        } catch (ProcessCanceledException | CancellationException ignore) {} catch (ExecutionException e) {
            log.error("Error whilst consuming LSP 'groovyScript/textureDecoration' request", e);
            return true;
        }
        return false;
    }

    /**
     * Adds results from `previousResults` to a inlayHintsSink.
     */
    private void addResultsToSink(Editor editor, InlayHintsSink inlayHintsSink) {
        var results = previousResults.getNow(Collections.emptyList());
        var doc = editor.getDocument();
        for (var el : results) {
            var presentation = new TextureElementPresentation(editor, el.getKey().getTextureUri(),
                    el.getKey().getTooltips());
            if (presentation.invalid()) continue;

            int offset;
            if (el.getValue() == null)
                offset = LSPIJUtils.toOffset(el.getKey().getRange().getStart(), doc);
            else
                offset = el.getValue().getTextRange().getStartOffset();

            inlayHintsSink.addInlineElement(offset, false,
                    presentation,
                    false);
        }
    }

    /**
     * Sends a new request to the LSP for inlay hints, with necessary cached values set and cancellations performed.
     */
    private void handleNewRequest(Editor editor, PsiFile psiFile, VirtualFile virtualFile, String path, long modStamp) {
        var params = new GetTextureParams();
        params.setTextDocument(new TextDocumentIdentifier(LSPIJUtils.toUriAsString(virtualFile)));

        var status = LanguageServerManager.getInstance(psiFile.getProject())
                .getServerStatus("groovyscript");

        if (status != ServerStatus.started) {
            // Not initialized yet; reset prev results
            previousResults = CompletableFuture.completedFuture(Collections.emptyList());
            return;
        }

        // cancel any existing requests
        // reduces load on intelliJ, does nothing on LSP
        if (!previousResults.isDone())
            previousResults.cancel(true);

        previousFile.set(path);
        previousSavedModStamp.set(modStamp);

        previousResults = getTextureHints(psiFile, params)
                .thenApplyAsync(result -> {
                    if (modStamp != previousSavedModStamp.get()) return Collections.emptyList();

                    var doc = editor.getDocument();
                    List<Pair<GetTextureResponse, PsiElement>> elements = new ArrayList<>();

                    for (var res : result) {
                        var offset = LSPIJUtils.toOffset(res.getRange().getStart(), doc);
                        var el = psiFile.findElementAt(offset);

                        if (el == null)
                            // Still add the inlay, we can use the doc offset as a backup
                            elements.add(Pair.of(res, null));
                        else
                            elements.add(Pair.of(res, el));
                    }

                    return elements;
                });
    }

    /**
     * Gets texture hints from the LSP.
     */
    private CompletableFuture<List<GetTextureResponse>> getTextureHints(PsiFile file, GetTextureParams params) {
        return LanguageServerManager.getInstance(file.getProject())
                .getLanguageServer("groovyscript")
                .thenApplyAsync(languageServerItem -> languageServerItem != null ?
                        languageServerItem.getServer() : null)
                .thenComposeAsync(ls -> {
                    if (ls == null) {
                        return CompletableFuture.completedFuture(Collections.emptyList());
                    }
                    GrSServerAPI myServer = (GrSServerAPI) ls;
                    return myServer.getTextureDecoration(params);
                });
    }
}
