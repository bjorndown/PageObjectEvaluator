package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.Executor;
import com.intellij.execution.JavaRunConfigurationBase;
import com.intellij.execution.JavaRunConfigurationExtensionManager;
import com.intellij.execution.ShortenCommandLine;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO rather back to extending ModuleBasedConfiguration again?
public class PageObjectRunConfig extends JavaRunConfigurationBase {

    public static final String MAIN_CLASS_NAME = "com.intellij.plugins.pageObjectEvaluator.RunPageObjectMain";
    private static final String RUN_CLASS_ELEMENT_NAME = "pageObjectClass";
    private static final String HTML_FILE_ELEMENT_NAME = "htmlFile";
    private static final String HTML_SNIPPET_ELEMENT_NAME = "htmlSnippet";
    private final RunConfigData runConfigData; // TODO use RunConfigurationOptions ?

    public PageObjectRunConfig(String name, ConfigurationFactory factory, Project project) {
        super(name, new JavaRunConfigurationModule(project, false), factory);
        runConfigData = new RunConfigData();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (getOptions().getModule() == null) {
            throw new RuntimeConfigurationError("Module must be selected");
        }

        if (getPageObjectClass().isBlank()) {
            throw new RuntimeConfigurationError("PageObject class must be selected");
        }

        var htmlFileIsBlank = getHtmlFile().isBlank();
        var htmlSnippetIsBlank = getHtmlSnippet().isBlank();

        if (htmlFileIsBlank && htmlSnippetIsBlank) {
            throw new RuntimeConfigurationError("Either a valid HTML file must be selected or an HTML snippet must be entered");
        }

        if (!htmlFileIsBlank && !htmlSnippetIsBlank) {
            throw new RuntimeConfigurationWarning("Both HTML file and HTML snippets are entered, HTML file will be ignored");
        }

        if (htmlSnippetIsBlank && !htmlFileIsBlank && !FileUtil.exists(getHtmlFile())) {
            throw new RuntimeConfigurationError("HTML file must exist");
        }
    }

    @Override
    public Collection<Module> getValidModules() {
        return Arrays.asList(ModuleManager.getInstance(getProject()).getModules());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new PageObjectConfigurable(this, getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor,
                                    @NotNull ExecutionEnvironment environment) {
        return new PageObjectRunState(environment, this);
    }

    @Override
    public void setVMParameters(String value) {
        runConfigData.setVMParameters(value);
    }

    @Override
    public String getVMParameters() {
        return runConfigData.getVMParameters();
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        return runConfigData.isAlternativeJrePathEnabled();
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean enabled) {
        runConfigData.setAlternativeJrePathEnabled(enabled);
    }

    @Override
    public String getAlternativeJrePath() {
        return runConfigData.getAlternativeJrePath();
    }

    @Override
    public void setAlternativeJrePath(String path) {
        runConfigData.setAlternativeJrePath(path);
    }

    @Override
    public @Nullable String getRunClass() {
        return MAIN_CLASS_NAME;
    }

    @Nullable
    @Override
    public String getPackage() {
        return runConfigData.getPackage();
    }

    @Override
    public void setProgramParameters(@Nullable String value) {
        runConfigData.setProgramParameters(value);
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return runConfigData.getProgramParameters();
    }

    @Override
    public void setWorkingDirectory(@Nullable String value) {
        runConfigData.setWorkingDirectory(value);
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return runConfigData.getWorkingDirectory();
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> envs) {
        runConfigData.setEnvs(envs);
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return runConfigData.getEnvs();
    }

    @Override
    public void setPassParentEnvs(boolean passParentEnvs) {
        runConfigData.setPassParentEnvs(passParentEnvs);
    }

    @Override
    public boolean isPassParentEnvs() {
        return runConfigData.isPassParentEnvs();
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
        writePageObjectClass(element);
        writeHtmlFile(element);
        writeHtmlSnippet(element);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    private void writePageObjectClass(Element element) {
        Element runClass = new Element(RUN_CLASS_ELEMENT_NAME);
        runClass.setText(runConfigData.getPageObjectClass());
        element.addContent(runClass);
    }

    private void writeHtmlFile(Element element) {
        Element htmlFile = new Element(HTML_FILE_ELEMENT_NAME);
        htmlFile.setText(runConfigData.getHtmlFile());
        element.addContent(htmlFile);
    }

    private void writeHtmlSnippet(Element element) {
        Element htmlSnippetElement = new Element(HTML_SNIPPET_ELEMENT_NAME);
        htmlSnippetElement.setText(runConfigData.getHtmlSnippet());
        element.addContent(htmlSnippetElement);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().readExternal(this, element);
        readModule(element);
        readPageObjectClass(element);
        readHtmlFile(element);
        readHtmlSnippet(element);
    }

    private void readPageObjectClass(Element element) {
        Element runClass = element.getChild(RUN_CLASS_ELEMENT_NAME);
        runConfigData.setPageObjectClass(runClass.getText());
    }

    private void readHtmlFile(Element element) {
        Element htmlFileElement = element.getChild(HTML_FILE_ELEMENT_NAME);
        runConfigData.setHtmlFile(htmlFileElement.getText());
    }

    private void readHtmlSnippet(Element element) {
        Element htmlFileElement = element.getChild(HTML_SNIPPET_ELEMENT_NAME);
        runConfigData.setHtmlSnippet(htmlFileElement.getText());
    }

    public String getPageObjectClass() {
        return runConfigData.getPageObjectClass();
    }

    public void setPageObjectClass(String runClass) {
        runConfigData.setPageObjectClass(runClass);
    }

    public String getHtmlFile() {
        return runConfigData.getHtmlFile();
    }

    public void setHtmlFile(String htmlFile) {
        runConfigData.setHtmlFile(htmlFile);
    }

    @Override
    public @Nullable ShortenCommandLine getShortenCommandLine() {
        return null;
    }

    @Override
    public void setShortenCommandLine(@Nullable ShortenCommandLine mode) {

    }

    public void setHtmlSnippet(String htmlSnippet) {
        runConfigData.setHtmlSnippet(htmlSnippet);
    }

    public String getHtmlSnippet() {
        return runConfigData.getHtmlSnippet();
    }

    public static class RunConfigData {

        private boolean passParentEnvs;
        private Map<String, String> envs = new HashMap<>();
        private String workingDirectory;
        private String programParameters;
        private String VMParameters;
        private boolean alternativeJrePathEnabled;
        private String alternativeJrePath;
        private String pageObjectClass;
        private String aPackage;
        private String htmlFile;
        private String htmlSnippet;

        public void setEnvs(Map<String, String> envs) {
            this.envs = envs;
        }

        public Map<String, String> getEnvs() {
            return envs;
        }

        public void setWorkingDirectory(String workingDirectory) {
            this.workingDirectory = workingDirectory;
        }

        public String getWorkingDirectory() {
            return workingDirectory;
        }

        public void setProgramParameters(String programParameters) {
            this.programParameters = programParameters;
        }

        public String getProgramParameters() {
            return programParameters;
        }

        public void setVMParameters(String VMParameters) {
            this.VMParameters = VMParameters;
        }

        public String getVMParameters() {
            return VMParameters;
        }

        public void setAlternativeJrePathEnabled(boolean alternativeJrePathEnabled) {
            this.alternativeJrePathEnabled = alternativeJrePathEnabled;
        }

        public boolean isAlternativeJrePathEnabled() {
            return alternativeJrePathEnabled;
        }

        public void setAlternativeJrePath(String alternativeJrePath) {
            this.alternativeJrePath = alternativeJrePath;
        }

        public String getAlternativeJrePath() {
            return alternativeJrePath;
        }

        public boolean isPassParentEnvs() {
            return passParentEnvs;
        }

        public void setPassParentEnvs(boolean passParentEnvs) {
            this.passParentEnvs = passParentEnvs;
        }

        public String getPageObjectClass() {
            return pageObjectClass;
        }

        public void setPageObjectClass(String pageObjectClass) {
            this.pageObjectClass = pageObjectClass;
        }

        public String getPackage() {
            return aPackage;
        }

        public String getaPackage() {
            return aPackage;
        }

        public void setaPackage(String aPackage) {
            this.aPackage = aPackage;
        }

        public String getHtmlFile() {
            return htmlFile;
        }

        public void setHtmlFile(String htmlFile) {
            this.htmlFile = htmlFile;
        }

        public String getHtmlSnippet() {
            return htmlSnippet;
        }

        public void setHtmlSnippet(String htmlSnippet) {
            this.htmlSnippet = htmlSnippet;
        }
    }
}
