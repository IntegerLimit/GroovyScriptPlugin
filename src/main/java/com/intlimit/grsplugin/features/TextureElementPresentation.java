package com.intlimit.grsplugin.features;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.codeInsight.hints.presentation.BasePresentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.HintHint;
import com.intellij.ui.LightweightHint;

@SuppressWarnings("UnstableApiUsage")
public class TextureElementPresentation extends BasePresentation {

    private static final Logger log = Logger.getInstance(TextureElementPresentation.class);
    private static final int BUFFER = 4;
    private static final Pattern imgMatcher = Pattern.compile("!\\[]\\((file:.+)\\)\\s");

    @Nullable
    private final ImageIcon img;
    private final List<Pair<String, ImageIcon>> tooltips;
    private final Editor editor;
    private LightweightHint hint;

    public TextureElementPresentation(Editor editor, String uri, List<String> tooltips) {
        this.editor = editor;
        this.img = getImg(uri);

        this.tooltips = new ArrayList<>();
        for (var tooltip : tooltips) {
            var matcher = imgMatcher.matcher(tooltip);
            ImageIcon icon = null;
            if (matcher.find()) {
                tooltip = matcher.replaceFirst("").trim();
                icon = getImg(matcher.group(1));
            }
            this.tooltips.add(Pair.of(tooltip, icon));
        }
    }

    public boolean invalid() {
        return img == null || tooltips.isEmpty();
    }

    @Nullable
    private static ImageIcon getImg(String uri) {
        try {
            return new ImageIcon(ImageIO.read(new File(new URI(uri))));
        } catch (IOException e) {
            log.error("Error trying to read texture from uri: " + uri);
            log.error(e);
            return null;
        } catch (URISyntaxException e) {
            log.error("Invalid uri: " + uri);
            log.error(e);
            return null;
        }
    }

    @Override
    public int getHeight() {
        if (img == null) return 0;
        return img.getIconHeight() + BUFFER;
    }

    @Override
    public int getWidth() {
        if (img == null) return 0;
        return img.getIconWidth() + BUFFER;
    }

    @Override
    public void paint(@NotNull Graphics2D graphics2D, @NotNull TextAttributes textAttributes) {
        if (img == null) {
            return;
        }
        img.paintIcon(null, graphics2D, BUFFER / 2, BUFFER / 2);
    }

    @Override
    public void mouseMoved(@NotNull MouseEvent event, @NotNull Point translated) {
        super.mouseMoved(event, translated);
        if (!(hint == null || !hint.isVisible()) || !editor.getContentComponent().isShowing()) return;

        createHint(event);
    }

    @Override
    public void mouseExited() {
        super.mouseExited();
        if (hint != null) {
            hint.hide();
            hint = null;
        }
    }

    private void createHint(MouseEvent event) {
        var imagePanel = new TextureElementPanel(tooltips);

        if (imagePanel.needsScrolling()) {
            hint = new LightweightHint(new TextureElementScrollPane(imagePanel)) {

                @Override
                public void show(@NotNull JComponent parentComponent, int x, int y, JComponent focusBackComponent,
                                 @NotNull HintHint hintHint) {
                    super.show(parentComponent, x, y, focusBackComponent, hintHint);
                    ((TextureElementScrollPane) getComponent()).startIfNeeded();
                }

                @Override
                public void hide() {
                    super.hide();
                    ((TextureElementScrollPane) getComponent()).stop();
                }
            };
        } else
            hint = new LightweightHint(imagePanel);

        var constraint = HintManager.ABOVE;
        var pointOnEditor = locationAt(editor.getContentComponent(), event);
        var point = HintManagerImpl.getHintPosition(hint, editor, editor.xyToVisualPosition(pointOnEditor), constraint);
        point.x = (int) (event.getXOnScreen() -
                editor.getContentComponent().getTopLevelAncestor().getLocationOnScreen().getX());

        HintManagerImpl.getInstanceImpl().showEditorHint(hint, editor, point,
                HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE | HintManager.HIDE_BY_SCROLLING,
                0,
                false,
                HintManagerImpl.createHintHint(editor, point, hint, constraint).setContentActive(false));
    }

    private Point locationAt(JComponent component, MouseEvent event) {
        var pointOnScreen = component.getLocationOnScreen();
        return new Point((int) (event.getXOnScreen() - pointOnScreen.getX()),
                (int) (event.getYOnScreen() - pointOnScreen.getY()));
    }
}
