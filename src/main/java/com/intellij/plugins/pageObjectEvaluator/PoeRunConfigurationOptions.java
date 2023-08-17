package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.configurations.ModuleBasedConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

// TODO find out how to properly use this
// https://plugins.jetbrains.com/docs/intellij/run-configurations.html
public class PoeRunConfigurationOptions extends ModuleBasedConfigurationOptions {

  private final StoredProperty<String> myScriptName =
      string("").provideDelegate(this, "scriptName");

  public String getScriptName() {
    return myScriptName.getValue(this);
  }

  public void setScriptName(String scriptName) {
    myScriptName.setValue(this, scriptName);
  }

}
