package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.impl.FileChooserFactoryImpl;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;

public class HtmlFileProvider {
    public String buildFromPastedHtml() {
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

    public String getFromFileChooser() {
        FileChooserDialog fileChooser = FileChooserFactoryImpl.getInstance().createFileChooser(
                FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), null, null);
        VirtualFile[] chosenFile = fileChooser.choose(null);
        return chosenFile[0].getUrl();
    }
}
