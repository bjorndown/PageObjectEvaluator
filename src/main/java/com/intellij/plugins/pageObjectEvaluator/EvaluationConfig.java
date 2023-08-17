package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.configurations.ParametersList;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class EvaluationConfig {

    public static final String FILE_PROTOCOL = "file://";
    public static final String FILE_PATH_ARG_NAME = "htmlFilePath";
    public static final String CLASS_NAME_ARG_NAME = "className";
    public static final String MODULE_OUTPUT_PATH_ARG_NAME = "moduleOutputPath";

    private final String classname;
    private final String moduleOutputPath;
    private final String htmlFilePath;

    public static EvaluationConfig from(String[] args) {
        String htmlFilePath = getCommandLineArg(FILE_PATH_ARG_NAME, args);
        String className = getCommandLineArg(CLASS_NAME_ARG_NAME, args);
        String moduleOutputPath = getCommandLineArg(MODULE_OUTPUT_PATH_ARG_NAME, args);
        return new EvaluationConfig(className, moduleOutputPath, htmlFilePath);
    }

    @Nullable
    private static String getCommandLineArg(@NotNull String argName, @NotNull String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].endsWith(argName) && i + 1 < args.length) {
                return args[i + 1];
            }
        }
        return null;
    }

    public static EvaluationConfig from(PageObjectRunConfig runConfig) {
        return new EvaluationConfig(runConfig.getPageObjectClass(), getModuleOutputPath(runConfig), getHtmlFilePath(runConfig));
    }

    private static String getHtmlFilePath(PageObjectRunConfig runConfig) {
        if (runConfig.getHtmlSnippet().isBlank()) {
            return FILE_PROTOCOL + runConfig.getHtmlFile();
        } else {
            return createTemporaryFileFromHtmlSnippet(runConfig);
        }
    }

    private static String createTemporaryFileFromHtmlSnippet(PageObjectRunConfig runConfig) {
        String snippet = runConfig.getHtmlSnippet();
        try {
            File tempFile = FileUtil.createTempFile("pageObjectEvaluator", ".html", true);
            FileUtil.writeToFile(tempFile, snippet);
            return tempFile.toURI().toURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getModuleOutputPath(PageObjectRunConfig runConfig) {
        Module module = runConfig.getConfigurationModule().getModule();
        return FILE_PROTOCOL + CompilerPaths.getModuleOutputPath(module, false);
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

    public void populateParameterList(ParametersList programParametersList) {
        programParametersList.add(FILE_PATH_ARG_NAME, getHtmlFilePath());
        programParametersList.add(CLASS_NAME_ARG_NAME, getClassname());
        programParametersList.add(MODULE_OUTPUT_PATH_ARG_NAME, getModuleOutputPath());
    }
}
