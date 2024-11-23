package com.intlimit.grsplugin.features;

import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.TimerUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An vertical infinite looping, vertical auto-scrolling, scroll pane.
 */
public class TextureElementScrollPane extends JScrollPane implements ActionListener {

    // Delay before scrolling starts (ms)
    private static final int SCROLL_INITIAL_INTERVAL = 1000;

    // Delay between each autoscroll (ms)
    private static final int SCROLL_UPDATE_INTERVAL = 15;

    // Scroll amount for each autoscroll
    private static final int SCROLL_AMOUNT = 2;

    private final TextureElementPanel panel;
    private final Timer timer;

    public TextureElementScrollPane(TextureElementPanel comp) {
        super(comp);
        panel = comp;
        panel.duplicate();

        var panelBorder = panel.getBorder();
        setBorder(panelBorder);
        panel.setBorder(JBUI.Borders.empty());

        setPreferredSize(new Dimension(panel.getViewportSize()));
        setBackground(comp.getBackground());

        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER;
        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_NEVER;

        timer = TimerUtil.createNamedTimer("GrSAutoScroller", SCROLL_UPDATE_INTERVAL, this);
        timer.setInitialDelay(SCROLL_INITIAL_INTERVAL);
    }

    public void startIfNeeded() {
        if (!timer.isRunning()) timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (verticalScrollBar == null) return;

        var scroll = verticalScrollBar.getValue() + SCROLL_AMOUNT;
        if (scroll > panel.getActualHeight()) {
            scroll -= panel.getActualHeight();
        }

        verticalScrollBar.setValue(scroll);
    }

    @Override
    public boolean isWheelScrollingEnabled() {
        return false;
    }
}
