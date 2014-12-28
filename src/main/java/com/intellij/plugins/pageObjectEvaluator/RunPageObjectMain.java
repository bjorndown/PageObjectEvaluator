package com.intellij.plugins.pageObjectEvaluator;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RunPageObjectMain {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        EvaluationConfig config = EvaluationConfig.from(args);
        new PageObjectEvaluator(config).evaluate();
    }
}
