import com.intellij.compiler.make.MakeUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class EvaluatePageObjectAction extends AnAction {

    private static final Logger LOG = Logger.getLogger(EvaluatePageObjectAction.class.getClass());
    public static final String FILE_PROTOCOL = "file://";
    private Project project;

    public void actionPerformed(AnActionEvent e) {
        PsiJavaFile psiFile = getPsiFile(e);

        if (psiFile == null) {
            return;
        }

        try {
            Class clazz = loadClass(psiFile);
            Object pageObject = populatePageObject(clazz);
            printOutputOfAllPublicNonArgMethodsToLog(pageObject);
        } catch (ClassNotFoundException | MalformedURLException | InvocationTargetException | IllegalAccessException e1) {
            LOG.error("Error ", e1);
        }
    }

    private PsiJavaFile getPsiFile(AnActionEvent e) {
        project = e.getProject();
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
        driver.get(FILE_PROTOCOL + getFileToEvaluate());
        return PageFactory.initElements(driver, clazz);
    }

    private String getFileToEvaluate() {
        return project.getBasePath() + "/test.html";
    }

    private Class loadClass(PsiJavaFile psiFile) throws MalformedURLException, ClassNotFoundException {
        URL url = new URL(FILE_PROTOCOL + getModuleOutputPath(psiFile) + "/");
        UrlClassLoader classloader = UrlClassLoader.build().urls(url).parent(this.getClass().getClassLoader()).get();
        return classloader.loadClass(buildFullyQualifiedClassName(psiFile));
    }

    private void printOutputOfAllPublicNonArgMethodsToLog(Object pageObject) throws IllegalAccessException, InvocationTargetException {
        for (Method method : pageObject.getClass().getMethods()) {
            if (method.getParameterCount() == 0 && shouldBeCalled(method)) {
                try {
                    LOG.info("Method '" + method.getName() + "': " + method.invoke(pageObject));
                } catch (Exception e) {
                    LOG.error("Exception while invoking method '" + method.getName() + "'", e);
                }
            }
        }
    }

    private boolean shouldBeCalled(Method method) {
        return !Arrays.asList("wait", "notify", "notifyAll").contains(method.getName());
    }

    private String buildFullyQualifiedClassName(PsiJavaFile psiFile) {
        return psiFile.getClasses()[0].getQualifiedName();
    }

    private String getModuleOutputPath(PsiElement psiElement) {
        Module moduleForPsiElement = ModuleUtil.findModuleForPsiElement(psiElement);
        return MakeUtil.getModuleOutputDirPath(moduleForPsiElement);
    }
}
