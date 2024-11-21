package com.intlimit.grsplugin.server;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GrSServerAPI extends LanguageServer {

    @JsonRequest("groovyScript/textureDecoration")
    CompletableFuture<List<GetTextureResponse>> getTextureDecoration(GetTextureParams params);
}
