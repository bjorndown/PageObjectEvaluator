package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.openapi.editor.impl.LineSet;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;

import java.util.HashMap;
import java.util.Map;

public class LineToMethodOutputMapper {
    private final HashMap<String, Object> methodObjectMap;
    private PsiJavaFile psiFile;
    private final LineSet lineSet;
    private Map<Integer, String> lineToOutputMap = new HashMap<>();

    public LineToMethodOutputMapper(HashMap<String, Object> methodObjectMap, PsiJavaFile psiFile, LineSet lineSet) {
        this.methodObjectMap = methodObjectMap;
        this.psiFile = psiFile;
        this.lineSet = lineSet;
        buildMap();
    }

    public String getOutputFor(int line) {
        return lineToOutputMap.get(line);
    }

    private void buildMap() {
        for (PsiMethod psiMethod : psiFile.getClasses()[0].getMethods()) {
            int lineMarkerInfo = lineSet.findLineIndex(psiMethod.getTextRange().getStartOffset());
            lineToOutputMap.put(lineMarkerInfo, String.valueOf(methodObjectMap.get(psiMethod.getName())));
        }
    }
}
