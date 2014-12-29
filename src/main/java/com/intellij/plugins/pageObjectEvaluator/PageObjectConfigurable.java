package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.ui.AlternativeJREPanel;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PageObjectConfigurable extends SettingsEditor<PageObjectRunConfig> implements PanelWithAnchor {
    private JPanel myWholePanel;
    private CommonJavaParametersPanel myCommonJavaParameters;
    private LabeledComponent<JComboBox> myModule;
    private AlternativeJREPanel myAlternativeJREPanel;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myClass;
    private LabeledComponent<TextFieldWithBrowseButton> myHtmlFile;
    private Project project;
    private JComponent anchor;
    private final ConfigurationModuleSelector myModuleSelector;

    public PageObjectConfigurable(Project project) {
        this.project = project;
        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());
        myCommonJavaParameters.setModuleContext(myModuleSelector.getModule());
        myCommonJavaParameters.setHasModuleMacro();
        myModule.getComponent().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myCommonJavaParameters.setModuleContext(myModuleSelector.getModule());
            }
        });
    }

    @Override
    public JComponent getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(JComponent anchor) {
        this.anchor = anchor;
    }

    @Override
    protected void resetEditorFrom(PageObjectRunConfig runConfig) {
        myClass.getComponent().setText(runConfig.getRunClass());
        myHtmlFile.getComponent().setText(runConfig.getHtmlFile());
        myCommonJavaParameters.reset(runConfig);
        myModuleSelector.reset(runConfig);

    }

    @Override
    protected void applyEditorTo(PageObjectRunConfig runConfig) throws ConfigurationException {
        runConfig.setRunClass(myClass.getComponent().getText());
        runConfig.setHtmlFile(myHtmlFile.getComponent().getText());
        myCommonJavaParameters.applyTo(runConfig);
        myModuleSelector.applyTo(runConfig);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myWholePanel;
    }

    private void createUIComponents() {
        createClassTextField();
        createHtmlFileTextField();
    }

    private void createHtmlFileTextField() {
        myHtmlFile = new LabeledComponent<TextFieldWithBrowseButton>();
        TextFieldWithBrowseButton textfield = new TextFieldWithBrowseButton();
        textfield.addBrowseFolderListener("Select HTML file", null, project, new FileChooserDescriptor(true, false, false, false, false, false));
        myHtmlFile.setComponent(textfield);
    }

    private void createClassTextField() {
        myClass = new LabeledComponent<EditorTextFieldWithBrowseButton>();
        EditorTextFieldWithBrowseButton myClassBrowseTextField = new EditorTextFieldWithBrowseButton(project, true, JavaCodeFragment.VisibilityChecker.PROJECT_SCOPE_VISIBLE);
        myClass.setComponent(myClassBrowseTextField);
    }
}
