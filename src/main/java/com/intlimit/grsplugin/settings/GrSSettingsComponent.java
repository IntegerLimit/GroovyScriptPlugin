package com.intlimit.grsplugin.settings;

import javax.swing.*;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

public class GrSSettingsComponent {

    private final JPanel panel;
    private final JBCheckBox enable = new JBCheckBox(GrSSettingsBundle.message("settings.enable"));
    private final JBTextField port = new JBTextField();
    private final JBCheckBox statusBar = new JBCheckBox(GrSSettingsBundle.message("settings.status"));
    private final JBCheckBox preTexture = new JBCheckBox(GrSSettingsBundle.message("settings.pre_texture"));

    private static final String PORT_VALIDATION = GrSSettingsBundle.message("settings.port.validate");

    public GrSSettingsComponent(Disposable parentDisposable) {
        panel = FormBuilder.createFormBuilder()
                .addComponent(enable)
                .addLabeledComponent(new JBLabel(GrSSettingsBundle.message("settings.port")), port, 1, false)
                .addComponent(statusBar)
                .addComponent(preTexture)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        // Components initialization
        new ComponentValidator(parentDisposable).withValidator(() -> {
            String pt = port.getText();
            if (StringUtil.isNotEmpty(pt)) {
                try {
                    int portValue = Integer.parseInt(pt);
                    if (portValue >= 0 && portValue <= 65535) {
                        return null;
                    } else {
                        return new ValidationInfo(PORT_VALIDATION, port);
                    }
                } catch (NumberFormatException nfe) {
                    return new ValidationInfo(PORT_VALIDATION, port);
                }
            } else {
                return null;
            }
        }).installOn(port);
    }

    public JPanel getPanel() {
        return panel;
    }

    public JComponent getPreferredFocusedComponent() {
        return port;
    }

    public int port() {
        try {
            return Integer.parseInt(port.getText());
        } catch (NumberFormatException e) {
            return GrSSettings.DEFAULT_PORT;
        }
    }

    public void setPort(int newPort) {
        port.setText(Integer.toString(newPort));
    }

    public void setEnable(boolean enable) {
        this.enable.setSelected(enable);
    }

    public boolean enabled() {
        return enable.isSelected();
    }

    public boolean statusBar() {
        return this.statusBar.isSelected();
    }

    public void setStatusBar(boolean statusBar) {
        this.statusBar.setSelected(statusBar);
    }

    public boolean preTexture() {
        return preTexture.isSelected();
    }

    public void setPreTexture(boolean preTexture) {
        this.preTexture.setSelected(preTexture);
    }

    public boolean isModified(GrSSettings.State state) {
        return port() != state.port || enabled() != state.enable || statusBar() != state.statusBar ||
                preTexture() != state.preTexture;
    }
}
