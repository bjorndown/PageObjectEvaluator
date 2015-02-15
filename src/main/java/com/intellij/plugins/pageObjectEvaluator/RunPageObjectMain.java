package com.intellij.plugins.pageObjectEvaluator;


import org.apache.log4j.*;

public class RunPageObjectMain {

    public static void main(String[] args) {
        ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%m%n"));
        BasicConfigurator.configure(consoleAppender);
        Logger.getRootLogger().setLevel(Level.INFO);
        EvaluationConfig config = EvaluationConfig.from(args[0]);
        new PageObjectEvaluator(config).evaluate();
    }
}
