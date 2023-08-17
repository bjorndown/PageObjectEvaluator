package com.intellij.plugins.pageObjectEvaluator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PageObjectEvaluator {

    private final EvaluationConfig evaluationConfig;

    public PageObjectEvaluator(EvaluationConfig evaluationConfig) {
        this.evaluationConfig = evaluationConfig;
    }

    public void evaluate() {
        try {
            Class<?> clazz = loadClass();
            WebDriver driver = new HtmlUnitDriver();
            Object pageObject = initializePageObject(driver, clazz);
            tryToInjectWebDriver(pageObject, driver);
            HashMap<String, Object> methodNameToResult = callPageObjectMethods(pageObject);
            driver.close();
            printToConsole(methodNameToResult);
        } catch (Exception error) {
            throw new RuntimeException(MessageFormat.format("Error while evaluating {0}", evaluationConfig.getClassname()), error);
        }
    }

    private Object initializePageObject(WebDriver driver, Class<?> clazz) {
        driver.get(evaluationConfig.getHtmlFilePath());
        return PageFactory.initElements(driver, clazz);
    }

    private void printToConsole(HashMap<String, Object> methodNameToResult) {
        for (Map.Entry<String, Object> stringObjectEntry : methodNameToResult.entrySet()) {
            System.out.println(stringObjectEntry.getKey() + ": " + stringObjectEntry.getValue());
        }
    }

    private void tryToInjectWebDriver(Object pageObject, WebDriver driver) {
        for (Field field : pageObject.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(WebDriver.class)) {
                try {
                    if (field.canAccess(pageObject)) {
                        field.set(pageObject, driver);
                    } else {
                        field.setAccessible(true);
                        field.set(pageObject, driver);
                        field.setAccessible(false);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Could not inject WebDriver", e);
                }
            }
        }
    }

    private Class<?> loadClass() throws MalformedURLException, ClassNotFoundException {
        URL url = new URL(evaluationConfig.getModuleOutputPath());
        try (URLClassLoader classloader = new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader())) {
            return classloader.loadClass(evaluationConfig.getClassname());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, Object> callPageObjectMethods(Object pageObject) throws IllegalAccessException, InvocationTargetException {
        HashMap<String, Object> methodToOutputMap = new HashMap<>();
        for (Method method : pageObject.getClass().getMethods()) {
            if (method.getParameterTypes().length == 0 && shouldBeCalled(method)) {
                try {
                    methodToOutputMap.put(method.getName(), method.invoke(pageObject));
                } catch (Exception e) {
                    methodToOutputMap.put(method.getName(), e.getMessage());
                }
            }
        }
        return methodToOutputMap;
    }

    private boolean shouldBeCalled(Method method) {
        return !Arrays.asList("wait", "notify", "notifyAll", "toString", "hashCode", "getClass").contains(method.getName());
    }
}
