package com.intlimit.grsplugin.server;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkDoneProgressAndPartialResultParams;
import org.eclipse.lsp4j.util.ToStringBuilder;

import java.util.Objects;

@SuppressWarnings("unused")
public class GetTextureParams extends WorkDoneProgressAndPartialResultParams {
    private TextDocumentIdentifier textDocument;

    public TextDocumentIdentifier getTextDocument() {
        return textDocument;
    }

    public void setTextDocument(TextDocumentIdentifier textDocument) {
        this.textDocument = textDocument;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("textDocument", textDocument);
        b.add("workDoneToken", getWorkDoneToken());
        b.add("partialResultToken", getPartialResultToken());
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GetTextureParams that = (GetTextureParams) o;
        return Objects.equals(textDocument, that.textDocument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), textDocument);
    }
}
