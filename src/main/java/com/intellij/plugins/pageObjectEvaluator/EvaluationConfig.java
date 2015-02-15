package com.intellij.plugins.pageObjectEvaluator;

import com.google.gson.Gson;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;

public class EvaluationConfig {

    public static final String FILE_PROTOCOL = "file://";

    private String classname;
    private String moduleOutputPath;
    private String htmlFilePath;

    public static EvaluationConfig from(String args) {
        return new Gson().fromJson(args, EvaluationConfig.class);
    }

    public static EvaluationConfig from(PageObjectRunConfig runConfig) {
        return new EvaluationConfig(runConfig.getRunClass(), getModuleOutputPath(runConfig), getHtmlFilePath(runConfig));
    }

    private static String getHtmlFilePath(PageObjectRunConfig runConfig) {
        if (StringUtil.isEmpty(runConfig.getHtmlFile())) {
            return new HtmlFileProvider().buildFromPastedHtml();
        } else {
            return FILE_PROTOCOL + runConfig.getHtmlFile();
        }
    }

    private static String getModuleOutputPath(PageObjectRunConfig runConfig) {
        Module module = runConfig.getConfigurationModule().getModule();
        return FILE_PROTOCOL + CompilerPaths.getModuleOutputPath(module, false);
    }

    public String toArgs() {
        return new Gson().toJson(this);
    }

    private EvaluationConfig(String classname, String moduleOutputPath, String htmlFilePath) {
        this.classname = classname;
        this.moduleOutputPath = moduleOutputPath;
        this.htmlFilePath = htmlFilePath;
    }

    public String getClassname() {
        return classname;
    }

    public String getModuleOutputPath() {
        return moduleOutputPath;
    }

    public String getHtmlFilePath() {
        return htmlFilePath;
    }
}
