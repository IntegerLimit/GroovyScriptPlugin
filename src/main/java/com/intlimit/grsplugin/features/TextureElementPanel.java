package com.intlimit.grsplugin.features;

import com.intellij.codeInsight.hint.HintUtil;
import com.intellij.ui.HintHint;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.codeInsight.hint.HintUtil.createLabel;

public class TextureElementPanel extends JPanel {

    private static final int MAX_TOOLTIP_HEIGHT = 500;

    private final List<Pair<String, ImageIcon>> tooltips;
    private boolean duplicated;

    public TextureElementPanel(List<Pair<String, ImageIcon>> tooltips) {
        this.tooltips = tooltips;

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

    @SuppressWarnings("UnstableApiUsage")
    private HintUtil.HintLabel getHintLabel(Pair<String, ImageIcon> tooltip) {
        HintHint hintHint = HintUtil.getInformationHint();
        HintUtil.HintLabel label = createLabel(tooltip.getKey(), tooltip.getValue(), getBackground(), hintHint);
        label.setBackground(getBackground());
        return label;
    }
}
