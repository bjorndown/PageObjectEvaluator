package com.intellij.plugins.pageObjectEvaluator;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RunPageObjectMain {
    public static void main(String[] args) {
        try {
            BasicConfigurator.configure(); // To suppress log4j warning
            Logger.getRootLogger().setLevel(Level.INFO); // Keep webdriver quiet

            EvaluationConfig config = EvaluationConfig.from(args);
            new PageObjectEvaluator(config).evaluate();
        } catch (NoClassDefFoundError error) {
            System.out.printf(
                    "Caught NoClassDefFoundError for class '%s'. Ensure this run configuration uses the classpath of the correct module%n",
                    error.getMessage());
        }
    }
}
