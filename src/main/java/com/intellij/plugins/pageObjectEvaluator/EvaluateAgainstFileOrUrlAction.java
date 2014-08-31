package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.impl.FileChooserFactoryImpl;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.Validate;

public class EvaluateAgainstFileOrUrlAction extends AbstractEvaluateAction {

    @Override
    protected String getUrlToEvaluate() {
        FileChooserDialog fileChooser = FileChooserFactoryImpl.getInstance().createFileChooser(FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), null, null);
        VirtualFile[] chosenFile = fileChooser.choose(null, null);
        Validate.isTrue(chosenFile.length == 1, "FileChooser did not return one file");
        return FILE_PROTOCOL + chosenFile[0].getPresentableUrl();
    }
}
