package com.intellij.plugins.pageObjectEvaluator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.TextAnnotationGutterProvider;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

class MethodOutputGutterProvider implements TextAnnotationGutterProvider {
    private LineToMethodOutputMapper lineToMethodOutputMapper;

    MethodOutputGutterProvider(LineToMethodOutputMapper lineToMethodOutputMapper) {
        this.lineToMethodOutputMapper = lineToMethodOutputMapper;
    }

    @Nullable
    @Override
    public String getLineText(int line, Editor editor) {
        return lineToMethodOutputMapper.getOutputFor(line);
    }

    @Nullable
    @Override
    public String getToolTip(int line, Editor editor) {
        return null;
    }

    @Override
    public EditorFontType getStyle(int line, Editor editor) {
        return EditorFontType.PLAIN;
    }

    @Nullable
    @Override
    public ColorKey getColor(int line, Editor editor) {
        return null;
    }

    @Nullable
    @Override
    public Color getBgColor(int line, Editor editor) {
        return null;
    }

    @Override
    public List<AnAction> getPopupActions(int line, Editor editor) {
        return new ArrayList<>();
    }

    @Override
    public void gutterClosed() {
    }
}
