package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.String;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PageObjectRunConfig extends ModuleBasedConfiguration<JavaRunConfigurationModule> implements CommonJavaRunConfigurationParameters {
    public static final String MAIN_CLASS_NAME = "com.intellij.plugins.pageObjectEvaluator.RunPageObjectMain";
    private static final String RUN_CLASS_ELEMENT_NAME = "runClass";
    private static final String HTML_FILE_ELEMENT_NAME = "htmlFile";
    private RunConfigData runConfigData;

    public PageObjectRunConfig(String name, ConfigurationFactory factory, Project project) {
        super(name, new JavaRunConfigurationModule(project, false), factory);
        runConfigData = new RunConfigData();
    }

    @Override
    public Collection<Module> getValidModules() {
        return Arrays.asList(ModuleManager.getInstance(getProject()).getModules());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<PageObjectRunConfig> group = new SettingsEditorGroup<PageObjectRunConfig>();
        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new PageObjectConfigurable(getProject()));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<PageObjectRunConfig>());
        return group;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
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

    @Nullable
    @Override
    public String getRunClass() {
        return runConfigData.getRunClass();
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
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
        writeModule(element);
        writeRunClass(element);
        writeHtmlFile(element);
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    private void writeRunClass(Element element) {
        Element runClass = new Element(RUN_CLASS_ELEMENT_NAME);
        runClass.setText(runConfigData.getRunClass());
        element.addContent(runClass);
    }

    private void writeHtmlFile(Element element) {
        Element runClass = new Element(HTML_FILE_ELEMENT_NAME);
        runClass.setText(runConfigData.getHtmlFile());
        element.addContent(runClass);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        JavaRunConfigurationExtensionManager.getInstance().readExternal(this, element);
        readModule(element);
        readRunClass(element);
        readHtmlFile(element);
    }

    private void readRunClass(Element element) {
        Element runClass = element.getChild(RUN_CLASS_ELEMENT_NAME);
        runConfigData.setRunClass(runClass.getText());
    }

    private void readHtmlFile(Element element) {
        Element htmlFileElement = element.getChild(HTML_FILE_ELEMENT_NAME);
        runConfigData.setHtmlFile(htmlFileElement.getText());
    }

    public void setRunClass(String runClass) {
        runConfigData.setRunClass(runClass);
    }

    public String getHtmlFile() {
        return runConfigData.getHtmlFile();
    }

    public void setHtmlFile(String htmlFile) {
        runConfigData.setHtmlFile(htmlFile);
    }

    public static class RunConfigData {

        private boolean passParentEnvs;
        private Map<String, String> envs = new HashMap<String, String>();
        private String workingDirectory;
        private String programParameters;
        private String VMParameters;
        private boolean alternativeJrePathEnabled;
        private String alternativeJrePath;
        private String runClass;
        private String aPackage;
        private String htmlFile;

        public void setEnvs(Map<String,String> envs) {
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

        public String getRunClass() {
            return runClass;
        }

        public void setRunClass(String runClass) {
            this.runClass = runClass;
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
    }
}
