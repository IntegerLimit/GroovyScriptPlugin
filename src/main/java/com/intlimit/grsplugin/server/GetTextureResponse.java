package com.intlimit.grsplugin.server;

import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.util.ToStringBuilder;

@SuppressWarnings("unused")
public class GetTextureResponse {

    private Range range;
    private String textureUri;
    private List<String> tooltips;

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public String getTextureUri() {
        return textureUri;
    }

    public void setTextureUri(String textureUri) {
        this.textureUri = textureUri;
    }

    public List<String> getTooltips() {
        return tooltips;
    }

    public void setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);
        b.add("range", range);
        b.add("textureUri", textureUri);
        b.add("tooltips", tooltips);
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetTextureResponse that = (GetTextureResponse) o;
        return Objects.equals(range, that.range) && Objects.equals(textureUri, that.textureUri) &&
                Objects.equals(tooltips, that.tooltips);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, textureUri, tooltips);
    }
}
