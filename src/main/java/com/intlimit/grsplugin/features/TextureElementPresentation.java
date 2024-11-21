package com.intlimit.grsplugin.features;

import com.intellij.codeInsight.hints.presentation.BasePresentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class TextureElementPresentation extends BasePresentation {

    private static final Logger log = Logger.getInstance(TextureElementPresentation.class);
    private static final int BUFFER = 2;

    @Nullable
    private BufferedImage img;

    public TextureElementPresentation(String uri) {
        uri = uri.replaceFirst("file:", ""); // Strip `file:` from start of uri
        try {
            img = ImageIO.read(new File(uri));
        } catch (IOException e) {
            log.error(e);
            img = null;
        }
    }

    @Override
    public int getHeight() {
        if (img == null) return 0;
        return img.getHeight() + BUFFER;
    }

    @Override
    public int getWidth() {
        if (img == null) return 0;
        return img.getWidth() + BUFFER;
    }

    @Override
    public void paint(@NotNull Graphics2D graphics2D, @NotNull TextAttributes textAttributes) {
        if (img == null) {
            return;
        }
        graphics2D.drawImage(img, BUFFER / 2, BUFFER / 2, null);
    }
}
