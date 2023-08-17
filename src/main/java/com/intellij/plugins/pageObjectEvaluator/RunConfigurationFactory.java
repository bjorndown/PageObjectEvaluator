package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RunConfigurationFactory extends ConfigurationFactory {

    protected RunConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull String getId() {
        return PageObjectRunConfigType.ID;
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new PageObjectRunConfig("PageObjectEvaluation", this, project);
    }

    @Override
    public Class<? extends BaseState> getOptionsClass() {
        return PoeRunConfigurationOptions.class;
    }

}
