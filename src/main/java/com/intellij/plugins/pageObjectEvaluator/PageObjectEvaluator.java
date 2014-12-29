package com.intellij.plugins.pageObjectEvaluator;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PageObjectEvaluator {
    private static final Logger LOG = Logger.getLogger(PageObjectEvaluator.class);
    private EvaluationConfig evaluationConfig;

    public PageObjectEvaluator(EvaluationConfig evaluationConfig) {
        this.evaluationConfig = evaluationConfig;
    }

    public void evaluate() {
        try {
            Class clazz = loadClass();
            Object pageObject = populatePageObject(clazz);
            HashMap<String, Object> methodNameToResult = evaluatePageObject(pageObject);
            printToConsole(methodNameToResult);
        } catch (ClassNotFoundException e1) {
            LOG.error("Error ", e1);
        } catch (MalformedURLException e1) {
            LOG.error("Error ", e1);
        } catch (InvocationTargetException e1) {
            LOG.error("Error ", e1);
        } catch (IllegalAccessException e1) {
            LOG.error("Error ", e1);
        }
    }

    private void printToConsole(HashMap<String, Object> methodNameToResult) {
        for (Map.Entry<String, Object> stringObjectEntry : methodNameToResult.entrySet()) {
            LOG.info(stringObjectEntry.getKey() + ": " + stringObjectEntry.getValue());
        }
    }

    private Object populatePageObject(Class clazz) {
        WebDriver driver = new HtmlUnitDriver();
        driver.get(evaluationConfig.getHtmlFilePath());
        Object pageObject = PageFactory.initElements(driver, clazz);
        tryToInjectWebDriver(pageObject, driver);
        return pageObject;
    }

    private void tryToInjectWebDriver(Object pageObject, WebDriver driver) {
        for (Field field : pageObject.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(WebDriver.class)) {
                try {
                    if (field.isAccessible()) {
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

    private Class loadClass() throws MalformedURLException, ClassNotFoundException {
        URL url = new URL(evaluationConfig.getModuleOutputPath());
        URLClassLoader classloader = new URLClassLoader(new URL[] { url }, this.getClass().getClassLoader());
        return classloader.loadClass(evaluationConfig.getClassname());
    }

    private HashMap<String, Object> evaluatePageObject(Object pageObject) throws IllegalAccessException, InvocationTargetException {
        HashMap<String, Object> methodToOutputMap = new HashMap<String, Object>();
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
