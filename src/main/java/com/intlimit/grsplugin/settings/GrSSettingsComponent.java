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
    private final JBCheckBox enable = new JBCheckBox(
            "Enable on Groovy files for this project?");
    private final JBTextField port = new JBTextField();

    private static final String MESSAGE = "The port number must be between 0 and 65535";

    public GrSSettingsComponent(Disposable parentDisposable) {
        panel = FormBuilder.createFormBuilder()
                .addComponent(enable)
                .addLabeledComponent(new JBLabel("LSP port:"), port, 1, false)
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
                        return new ValidationInfo(MESSAGE, port);
                    }
                } catch (NumberFormatException nfe) {
                    return new ValidationInfo(MESSAGE, port);
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

    public int getPort() {
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

    public boolean isModified(GrSSettings.State state) {
        return getPort() != state.port || enabled() != state.enable;
    }
}
