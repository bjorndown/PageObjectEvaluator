import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.lang.UrlClassLoader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class EvalutePageObjectAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return;
        }

        Document document = editor.getDocument();
        if (document == null) {
            return;
        }
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (virtualFile == null) {
            return;
        }

        PsiJavaFile psiFile = (PsiJavaFile) PsiDocumentManager.getInstance(project).getPsiFile(document);

        URL url = null;
        try {
            url = new URL("file://" + project.getBasePath() + "/out/production/untitled/");
        } catch (MalformedURLException e1) {
            return;
        }
        UrlClassLoader classloader = UrlClassLoader.build().urls(url).parent(this.getClass().getClassLoader()).get();
        Class clazz = null;
        try {
            clazz = classloader.loadClass(psiFile.getPackageName() + "." + psiFile.getClasses()[0].getName());
        } catch (ClassNotFoundException e1) {
            return;
        }

        WebDriver driver = new HtmlUnitDriver();
        driver.get("file://" + project.getBasePath() + "/test.html");
        Object o = PageFactory.initElements(driver, clazz);
    }
}
