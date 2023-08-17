package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.application.JavaSettingsEditorBase;
import com.intellij.execution.ui.CommonParameterFragments;
import com.intellij.execution.ui.ModuleClasspathCombo;
import com.intellij.execution.ui.SettingsEditorFragment;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.LanguageTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

import static com.intellij.execution.ui.CommandLinePanel.setMinimumWidth;

public class PageObjectConfigurable extends JavaSettingsEditorBase<PageObjectRunConfig> {

    public static final String FRAGMENT_GROUP_NAME = "PageObjectEvaluator";
    private final Project project;

    public PageObjectConfigurable(PageObjectRunConfig pageObjectRunConfig, Project project) {
        super(pageObjectRunConfig);
        this.project = project;
    }

    @Override
    protected void customizeFragments(
            List<SettingsEditorFragment<PageObjectRunConfig, ?>> settingsEditorFragments,
            SettingsEditorFragment<PageObjectRunConfig, ModuleClasspathCombo> moduleClasspath,
            CommonParameterFragments<PageObjectRunConfig> commonParameterFragments) {

        settingsEditorFragments.add(getPageObjectClassFragment());
        settingsEditorFragments.add(getHtmlFileFragment());
        settingsEditorFragments.add(getHtmlTextFragment());
    }

    @NotNull
    private SettingsEditorFragment<PageObjectRunConfig, LabeledComponent<EditorTextFieldWithBrowseButton>> getPageObjectClassFragment() {
        var pageObjectClassTextField = new EditorTextFieldWithBrowseButton(
                project, true, JavaCodeFragment.VisibilityChecker.PROJECT_SCOPE_VISIBLE);
        setMinimumWidth(pageObjectClassTextField, 400);
        SettingsEditorFragment<PageObjectRunConfig, LabeledComponent<EditorTextFieldWithBrowseButton>> pageObjectClassFragment = new SettingsEditorFragment<>(
                "pageObjectClass", "Page object class", FRAGMENT_GROUP_NAME, wrapInLabel(pageObjectClassTextField, "PageObject Class"), 1,
                (configuration, component) -> component.getComponent().setText(
                        configuration.getPageObjectClass()),
                (configuration, component) -> configuration.setPageObjectClass(component.getComponent().getText()),
                configuration -> true);
        pageObjectClassFragment.setRemovable(false);
        pageObjectClassFragment.setActionDescription("PageObject class to evaluate");
        pageObjectClassFragment.setHint("Select PageObject class to evaluate");
        return pageObjectClassFragment;
    }

    @NotNull
    private SettingsEditorFragment<PageObjectRunConfig, LabeledComponent<EditorTextField>> getHtmlTextFragment() {
        var htmlTextField = new LanguageTextField(HTMLLanguage.INSTANCE, getProject(), "", false);
        setMinimumWidth(htmlTextField, 400);

        var htmlSnippetFragment = new SettingsEditorFragment<PageObjectRunConfig, LabeledComponent<EditorTextField>>(
                "htmlSnippet", "HTML snippet", FRAGMENT_GROUP_NAME, wrapInLabel(htmlTextField, "HTML Snippet"), 2,
                (configuration, component) -> component.getComponent().setText(
                        configuration.getHtmlSnippet()),
                (configuration, component) -> configuration.setHtmlSnippet(component.getComponent().getText()),
                configuration -> true);

        htmlSnippetFragment.setActionDescription("Evaluate this PageObject against an HTML snippet");
        htmlSnippetFragment.setHint("Write an HTML snippet to evaluate this PageObject against");
        return htmlSnippetFragment;
    }

    @NotNull
    private SettingsEditorFragment<PageObjectRunConfig, LabeledComponent<TextFieldWithBrowseButton>> getHtmlFileFragment() {
        var htmlFileTextField = new TextFieldWithBrowseButton();
        htmlFileTextField.addBrowseFolderListener("Select HTML File", "", project, FileChooserDescriptorFactory.createSingleFileDescriptor(HtmlFileType.INSTANCE));
        setMinimumWidth(htmlFileTextField, 400);
        var htmlFileFragment = new SettingsEditorFragment<PageObjectRunConfig, LabeledComponent<TextFieldWithBrowseButton>>(
                "htmlFile", "HTML file", FRAGMENT_GROUP_NAME, wrapInLabel(htmlFileTextField, "HTML File"), 3,
                (configuration, component) -> component.getComponent().setText(
                        configuration.getHtmlFile()),
                (configuration, component) -> configuration.setHtmlFile(component.getComponent().getText()),
                configuration -> false);
        htmlFileFragment.setActionDescription("Evaluate this PageObject against an HTML file");
        htmlFileFragment.setHint("Select an HTML file to evaluate this PageObject against");
        return htmlFileFragment;
    }

    private <T extends JComponent> LabeledComponent<T> wrapInLabel(T component, String labelText) {
        var label = new LabeledComponent<T>();
        label.setText(labelText);
        label.setComponent(component);
        return label;
    }
}
