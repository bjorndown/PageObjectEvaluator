package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PageObjectRunConfigType implements ConfigurationType {
    private ConfigurationFactory myConfigurationFactory;

    public PageObjectRunConfigType() {
        myConfigurationFactory = new ConfigurationFactoryEx(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new PageObjectRunConfig("", this, project);
            }
        };
    }

    @Override
    public String getDisplayName() {
        return "PageObject";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "PageObject run configuration";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.RunConfigurations.Web_app;
    }

    @NotNull
    @Override
    public String getId() {
        return "PageObjectEvaluator";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] { myConfigurationFactory };
    }
}
