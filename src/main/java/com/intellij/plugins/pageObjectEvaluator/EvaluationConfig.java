package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;

public class EvaluationConfig {

    private static final String CLASSNAME = "--classname";
    private static final String OUTPUT_PATH = "--output-path";
    private static final String HTML_FILE = "--html-file";
    public static final String SPACE = " ";
    public static final String FILE_PROTOCOL = "file://";

    private String classname;
    private String moduleOutputPath;
    private String htmlFilePath;

    public static EvaluationConfig from(String[] args) {
        return new EvaluationConfig(getClassname(args), getModuleOutputPath(args), getHtmlFilePath(args));
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

    private static String getHtmlFilePath(String[] args) {
        return getParameterValue(args, HTML_FILE);
    }

    private static String getModuleOutputPath(String[] args) {
        return getParameterValue(args, OUTPUT_PATH);
    }

    private static String getClassname(String[] args) {
        return getParameterValue(args, CLASSNAME);
    }

    private static String getParameterValue(String[] args, String parameterName) {
        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase(parameterName) && i < argsLength + 1) {
                return args[i + 1];
            }
        }
        throw new IllegalArgumentException("Argument '" + parameterName + "' not given.");
    }

    public String toArgs() {
        return CLASSNAME + SPACE + classname + SPACE + OUTPUT_PATH + SPACE + moduleOutputPath + SPACE + HTML_FILE + SPACE + htmlFilePath;
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
