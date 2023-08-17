package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PageObjectRunConfigType extends ConfigurationTypeBase {
    static final String ID = "PageObjectEvaluator";
    static final Icon icon = IconLoader.createLazy(() -> IconManager.getInstance().getIcon("META-INF/pluginIcon.svg", PageObjectRunConfigType.class));

    protected PageObjectRunConfigType() {
        super(ID, "PageObjectEvaluator", "Evaluates a PageObject without running a full selenium test test", icon);
        addFactory(new RunConfigurationFactory(this));
    }

    @NotNull
    public static PageObjectRunConfigType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(PageObjectRunConfigType.class);
    }
}
