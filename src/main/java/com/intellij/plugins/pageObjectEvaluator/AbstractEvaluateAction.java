package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.compiler.make.MakeUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.LineSet;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.lang.UrlClassLoader;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

abstract class AbstractEvaluateAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(AbstractEvaluateAction.class.getClass());
    public static final String FILE_PROTOCOL = "file://";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiJavaFile psiFile = getPsiFile(e);

        if (psiFile == null) {
            return;
        }
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        try {
            Class clazz = loadClass(psiFile);
            Object pageObject = populatePageObject(clazz);
            HashMap<String, Object> methodNameToResult = evaluatePageObject(pageObject);
            LineToMethodOutputMapper lineToMethodOutputMapper = new LineToMethodOutputMapper(methodNameToResult, psiFile, createLineSet(editor));
            editor.getGutter().registerTextAnnotation(new MethodOutputGutterProvider(lineToMethodOutputMapper));
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

    private LineSet createLineSet(Editor editor) {
        LineSet lineSet = new LineSet();
        lineSet.documentCreated(editor.getDocument());
        return lineSet;
    }

    private PsiJavaFile getPsiFile(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return null;
        }

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return null;
        }

        return (PsiJavaFile) PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    }

    private Object populatePageObject(Class clazz) {
        WebDriver driver = new HtmlUnitDriver();
        driver.get(getUrlToEvaluate());
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
                    LOG.error("Could not inject WebDriver", e);
                }
            }
        }
    }


    protected abstract String getUrlToEvaluate();

    private Class loadClass(PsiJavaFile psiFile) throws MalformedURLException, ClassNotFoundException {
        URL url = new URL(FILE_PROTOCOL + getModuleOutputPath(psiFile) + "/");
        UrlClassLoader classloader = UrlClassLoader.build().urls(url).parent(this.getClass().getClassLoader()).get();
        return classloader.loadClass(getFullyQualifiedClassName(psiFile));
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

    private String getFullyQualifiedClassName(PsiJavaFile psiFile) {
        return psiFile.getClasses()[0].getQualifiedName();
    }

    private String getModuleOutputPath(PsiElement psiElement) {
        Module moduleForPsiElement = ModuleUtil.findModuleForPsiElement(psiElement);
        return MakeUtil.getModuleOutputDirPath(moduleForPsiElement);
    }
}
