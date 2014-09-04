package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.IOException;

public class EvaluateAgainstPastedHtmlAction extends AbstractEvaluateAction {
    @Override
    protected String getUrlToEvaluate() {
        try {
            File tempFile = FileUtil.createTempFile("pageObjectEvaluator", ".html", true);
            PasteHtmlDialog  dialog = new PasteHtmlDialog();
            dialog.pack();
            dialog.setVisible(true);
            String text = dialog.getText();
            FileUtil.writeToFile(tempFile, text);
            return tempFile.toURI().toURL().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
