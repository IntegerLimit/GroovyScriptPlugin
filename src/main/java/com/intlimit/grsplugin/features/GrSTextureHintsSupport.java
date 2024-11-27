package com.intlimit.grsplugin.features;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intlimit.grsplugin.server.GetTextureParams;
import com.intlimit.grsplugin.server.GetTextureResponse;
import com.intlimit.grsplugin.server.GrSServerAPI;
import com.redhat.devtools.lsp4ij.LanguageServerManager;
import com.redhat.devtools.lsp4ij.features.AbstractLSPWorkspaceFeatureSupport;
import com.redhat.devtools.lsp4ij.internal.CancellationSupport;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GrSTextureHintsSupport extends AbstractLSPWorkspaceFeatureSupport<GetTextureParams, List<GetTextureResponse>> {

    private static final Key<GrSTextureHintsSupport> GRS_TEXTURE_HINTS_SUPPORT = Key.create("grs.lsp.texture.hints");
    private PsiFile file;
    private long modificationStamp;

    public GrSTextureHintsSupport(Project project, PsiFile file) {
        super(project);
        this.file = file;
        this.modificationStamp = -1;
    }

    public void updateFileIfNeeded(PsiFile file) {
        if (this.file == file) return;

        Logger.getInstance(getClass()).info("Setting new file");
        this.file = file;
        this.modificationStamp = -1;
    }

    public CompletableFuture<List<GetTextureResponse>> getTextureHints(GetTextureParams params) {
        return super.getFeatureData(params);
    }

    @Override
    protected synchronized CompletableFuture<List<GetTextureResponse>> load(GetTextureParams getTextureParams) {
        var future = super.load(getTextureParams);
        this.modificationStamp = this.file.getModificationStamp();
        return future;
    }

    @Override
    protected boolean checkValid() {
        return super.checkValid() && this.file.getModificationStamp() == this.modificationStamp;
    }

    @Override
    protected CompletableFuture<List<GetTextureResponse>> doLoad(GetTextureParams getTextureParams, CancellationSupport cancellationSupport) {
        return cancellationSupport.execute(
                LanguageServerManager.getInstance(file.getProject())
                        .getLanguageServer("groovyscript")
                        .thenApplyAsync(languageServerItem ->
                                languageServerItem != null ? languageServerItem.getServer() : null)
                        .thenComposeAsync(ls -> {
                            if (ls == null) {
                                return CompletableFuture.completedFuture(Collections.emptyList());
                            }
                            GrSServerAPI myServer = (GrSServerAPI) ls;
                            return myServer.getTextureDecoration(getTextureParams);
                        }));
    }

    /**
     * Return the existing GrS Texture Hints file support for the given Psi file, or create a new one if necessary.
     */
    public static @NotNull GrSTextureHintsSupport getSupport(@NotNull PsiFile file) {
        GrSTextureHintsSupport support = file.getProject().getUserData(GRS_TEXTURE_HINTS_SUPPORT);
        if (support == null) {
            // create support by taking care of multiple threads which could call it.
            support = createSupport(file);
        }
        // update support with new file
        support.updateFileIfNeeded(file);
        return support;
    }

    private synchronized static @NotNull GrSTextureHintsSupport createSupport(@NotNull PsiFile file) {
        var support = new GrSTextureHintsSupport(file.getProject(), file);

        file.getProject().putUserData(GRS_TEXTURE_HINTS_SUPPORT, support);
        return support;
    }
}
