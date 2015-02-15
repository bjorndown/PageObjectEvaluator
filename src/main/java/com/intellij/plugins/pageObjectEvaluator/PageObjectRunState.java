package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.application.BaseJavaApplicationCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.util.PathUtil;
import org.apache.log4j.Logger;

public class PageObjectRunState extends BaseJavaApplicationCommandLineState<PageObjectRunConfig> {
    public PageObjectRunState(ExecutionEnvironment environment, PageObjectRunConfig configuration) {
        super(environment, configuration);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters params = new JavaParameters();
        final JavaRunConfigurationModule module = myConfiguration.getConfigurationModule();
        final String jreHome = myConfiguration.isAlternativeJrePathEnabled() ? myConfiguration.getAlternativeJrePath() : null;
        final int classPathType = JavaParametersUtil.getClasspathType(module,
                PageObjectRunConfig.MAIN_CLASS_NAME,
                false);

        JavaParametersUtil.configureModule(module, params, classPathType, jreHome);
        params.setJdk(JavaParametersUtil.createProjectJdk(myConfiguration.getProject(), jreHome));
        params.setMainClass(PageObjectRunConfig.MAIN_CLASS_NAME);
        params.getProgramParametersList().add(EvaluationConfig.from(myConfiguration).toArgs());
        params.getClassPath().add(PathUtil.getJarPathForClass(RunPageObjectMain.class));
        params.getClassPath().add(PathUtil.getJarPathForClass(Logger.class));
        setupJavaParameters(params);

        return params;
    }
}
