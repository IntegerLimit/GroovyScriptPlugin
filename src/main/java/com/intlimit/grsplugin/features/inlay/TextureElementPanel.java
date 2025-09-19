package com.intlimit.grsplugin.features.inlay;

import static com.intellij.codeInsight.hint.HintUtil.createHintBorder;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import org.apache.commons.lang3.tuple.Pair;

import com.intellij.codeInsight.hint.HintUtil;
import com.intellij.ide.IdeTooltipManager;
import com.intellij.ui.*;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StartupUiUtil;

public class TextureElementPanel extends JPanel {

    private static final int MAX_TOOLTIP_HEIGHT = 500;

    private final List<Pair<String, ImageIcon>> tooltips;
    private boolean duplicated;

    public TextureElementPanel(List<Pair<String, ImageIcon>> tooltips) {
        this.tooltips = tooltips;

        setBackground(HintUtil.getInformationColor());
        setBorder(JBUI.Borders.empty(6, 6, 5, 6));
        var layout = new GridLayout(0, 1);
        layout.setVgap(6);
        setLayout(layout);

        this.duplicated = false;
        addTooltips();
    }

    private void addTooltips() {
        for (var tooltip : tooltips) {
            add(getHintLabel(tooltip));
        }
    }

    public void duplicate() {
        if (duplicated) return;

        duplicated = true;

        // Add another instance of tooltips
        addTooltips();
    }

    public int getActualHeight() {
        return duplicated ? getPreferredSize().height / 2 : getPreferredSize().height;
    }

    public boolean needsScrolling() {
        return getPreferredSize().height > MAX_TOOLTIP_HEIGHT;
    }

    public Dimension getViewportSize() {
        return new Dimension(getPreferredSize().width, MAX_TOOLTIP_HEIGHT);
    }

    private HintLabel getHintLabel(Pair<String, ImageIcon> tooltip) {
        HintHint hintHint = HintUtil.getInformationHint();
        return new HintLabel(tooltip, getBackground(), hintHint);
    }

    /**
     * Essentially just HintUtil -> HintLabel, but extracted out (due to internal api), and cleaned up for personal use.
     */
    public static final class HintLabel extends JPanel {

        private JEditorPane pane;

        private HintLabel(Pair<String, ImageIcon> tooltip, Color color, HintHint hintHint) {
            setLayout(new BorderLayout(NewUI.isEnabled() ? 6 : 0, 0));
            setBackground(color);

            if (tooltip.getKey() != null)
                setText(tooltip.getKey(), hintHint);
            if (tooltip.getValue() != null)
                setIcon(tooltip.getValue());

            if (!hintHint.isAwtTooltip()) {
                setBorder(createHintBorder());
                setForeground(JBColor.foreground());
                setFont(StartupUiUtil.getLabelFont().deriveFont(Font.BOLD));
                setOpaque(true);
            }
        }

        private void setText(String s, HintHint hintHint) {
            pane = IdeTooltipManager.initPane(s, hintHint, null);
            add(pane, BorderLayout.CENTER);

            setOpaque(true);
            setBackground(hintHint.getTextBackground());

            revalidate();
            repaint();
        }

        private void setIcon(Icon icon) {
            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);

            add(iconLabel, BorderLayout.WEST);

            revalidate();
            repaint();
        }

        @Override
        public boolean requestFocusInWindow() {
            if (pane == null) return super.requestFocusInWindow();
            return pane.requestFocusInWindow();
        }

        @Override
        public String toString() {
            return "Hint: text='" + getText() + "'";
        }

        public String getText() {
            return pane != null ? pane.getText() : "";
        }
    }
}
