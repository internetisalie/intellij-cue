package dev.monogon.cue.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.FormBuilder;
import dev.monogon.cue.Messages;

import javax.swing.*;

public class CueSettingsComponent implements CueSettings {
    private final JPanel myMainPanel;
    private final JTextField myCuePathText;
    private final JBCheckBox myFormatIgnoreErrors;
    private final JBCheckBox myFormatSimplifyOutput;

    public CueSettingsComponent() {
        var cuePathBrowseField = new TextFieldWithBrowseButton();

        cuePathBrowseField.addBrowseFolderListener(
            Messages.get("applicationSettings.cuePath.dialogTitle"),
            Messages.get("applicationSettings.cuePath.dialogLabel"),
            null,
            FileChooserDescriptorFactory.createSingleLocalFileDescriptor()
        );

        myCuePathText = cuePathBrowseField.getTextField();
        if (myCuePathText instanceof JBTextField) {
            ((JBTextField)myCuePathText).
                getEmptyText().
                setText(
                    Messages.get("applicationSettings.cuePath.emptyText")
                );
        }
        myCuePathText.setColumns(0); // fill width

        myFormatIgnoreErrors = new JBCheckBox("Ignore errors");
        myFormatSimplifyOutput = new JBCheckBox("Simplify output");

        var pathSectionBuilder = FormBuilder.createFormBuilder();
        pathSectionBuilder.getPanel().setBorder(IdeBorderFactory.createTitledBorder("Tool Executable"));
        pathSectionBuilder.addLabeledComponent(new JBLabel("Path to cue: "), myCuePathText, 1, false);

        var formatSectionBuilder = FormBuilder.createFormBuilder();
        formatSectionBuilder.getPanel().setBorder(IdeBorderFactory.createTitledBorder("Formatting"));
        formatSectionBuilder.addComponent(myFormatIgnoreErrors);
        formatSectionBuilder.addComponent(new Spacer());
        formatSectionBuilder.addComponent(myFormatSimplifyOutput);
        formatSectionBuilder.addComponent(new Spacer());

        var mainPanelBuilder = FormBuilder.createFormBuilder();
        mainPanelBuilder.getPanel().setLayout(new VerticalFlowLayout());
        mainPanelBuilder.addComponent(pathSectionBuilder.getPanel());
        mainPanelBuilder.addComponent(formatSectionBuilder.getPanel());
        mainPanelBuilder.addComponentFillVertically(new JPanel(), 0);
        myMainPanel = mainPanelBuilder.getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return myCuePathText;
    }

    public void setCueExecutablePath(String cuePath) {
        myCuePathText.setText(cuePath);
    }

    public String getCueExecutablePath() {
        return myCuePathText.getText();
    }

    public void setFormatIgnoreErrors(boolean formatIgnoreErrors) {
        myFormatIgnoreErrors.setSelected(formatIgnoreErrors);
    }

    public boolean getFormatIgnoreErrors() {
        return myFormatIgnoreErrors.isSelected();
    }

    public void setFormatSimplifyOutput(boolean formatSimplifyOutput) {
        myFormatSimplifyOutput.setSelected(formatSimplifyOutput);
    }

    public boolean getFormatSimplifyOutput() {
        return myFormatSimplifyOutput.isSelected();
    }
}
