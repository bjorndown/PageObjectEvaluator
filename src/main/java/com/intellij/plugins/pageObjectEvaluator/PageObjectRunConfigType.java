package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import org.jetbrains.annotations.NotNull;

public class PageObjectRunConfigType extends ConfigurationTypeBase {
    static final String ID = "PageObjectEvaluator";

    protected PageObjectRunConfigType() {
        super(ID, "PageObjectEvaluator", "Evaluates a PageObject without running a full selenium test test", PoeIcons.PLUGIN_ICON);
        addFactory(new RunConfigurationFactory(this));
    }

    @NotNull
    public static PageObjectRunConfigType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(PageObjectRunConfigType.class);
    }
}
