package com.intlimit.grsplugin.features;

import com.intellij.codeInsight.hints.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intlimit.grsplugin.server.GetTextureParams;
import com.intlimit.grsplugin.server.GetTextureResponse;
import com.intlimit.grsplugin.server.GrSServerAPI;
import com.redhat.devtools.lsp4ij.LSPIJUtils;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.redhat.devtools.lsp4ij.internal.CompletableFutures.isDoneNormally;
import static com.redhat.devtools.lsp4ij.internal.CompletableFutures.waitUntilDone;

@SuppressWarnings("UnstableApiUsage")
// TODO: With LSP4IJ Update, Consider Moving to Just AbstractLSPInlayHintsProvider, and use PendingFutures.
public class TextureHintsCollector extends FactoryInlayHintsCollector {

    private static final Logger log = Logger.getInstance(TextureHintsCollector.class);
    private boolean processed;
    private final Editor editor;
    private final PsiFile file;

    public TextureHintsCollector(@NotNull Editor editor, @NotNull PsiFile file) {
        super(editor);
        this.editor = editor;
        this.file = file;
        this.processed = false;
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (processed) {
            return false;
        }
        processed = true;

        Project project = file.getProject();
        if (project.isDisposed()) {
            return false;
        }

        doCollect(inlayHintsSink);
        return false;
    }

    protected void doCollect(@NotNull InlayHintsSink inlayHintsSink) {
        var virtualFile = file.getVirtualFile();
        if (virtualFile == null) return;

        var params = new GetTextureParams();
        params.setTextDocument(LSPIJUtils.toTextDocumentIdentifier(virtualFile));

        CompletableFuture<List<GetTextureResponse>> future = LanguageServerManager.getInstance(file.getProject())
                .getLanguageServer("groovyscript")
                .thenApply(languageServerItem ->
                        languageServerItem != null ? languageServerItem.getServer() : null)
                .thenComposeAsync(ls -> {
                    if (ls == null) {
                        return CompletableFuture.completedFuture(Collections.emptyList());
                    }
                    GrSServerAPI myServer = (GrSServerAPI) ls;
                    return myServer.getTextureDecoration(params);
                });

        try {
            waitUntilDone(future);

            if (!isDoneNormally(future)) return;

            var result = future.getNow(Collections.emptyList());
            var doc = editor.getDocument();

            for (var res : result) {
                var offset = LSPIJUtils.toOffset(res.getRange().getStart(), doc);
                var presentation = new TextureElementPresentation(editor, res.getTextureUri(), res.getTooltips());
                inlayHintsSink.addInlineElement(offset, false,
                        presentation,
                        false);
            }
        } catch (ProcessCanceledException | CancellationException ignore) {

        } catch (ExecutionException e) {
            log.error("Error whilst consuming LSP 'groovyScript/textureDecoration' request", e);
        }
    }
}
