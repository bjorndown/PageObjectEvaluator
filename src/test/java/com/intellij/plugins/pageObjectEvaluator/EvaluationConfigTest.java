package com.intellij.plugins.pageObjectEvaluator;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationConfigTest {
    @Test
    public void shouldParseArgsCorrectly() {
        String[] args = {"--htmlFilePath", "asd", "--moduleOutputPath", "/tmp/bla", "--className", "TestClass"};
        EvaluationConfig testee = EvaluationConfig.from(args);

        assertEquals(args[1], testee.getHtmlFilePath());
        assertEquals(args[3], testee.getModuleOutputPath());
        assertEquals(args[5], testee.getClassname());
    }

    @Test
    public void shouldReturnNullIfArgNotFound() {
        String[] args = {};
        EvaluationConfig testee = EvaluationConfig.from(args);

        assertNull(testee.getHtmlFilePath());
        assertNull(testee.getModuleOutputPath());
        assertNull(testee.getClassname());
    }
}