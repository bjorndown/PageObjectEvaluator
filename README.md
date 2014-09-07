PageObjectEvaluator
===================

Allows evaluating PageObjects (POJOs containing @FindBy) without starting a full Selenium test run, just feed it some HTML.

![Screenshot](/doc/screenshot.png)

- Evaluates @FindBy* by calling PageFactory.initElements()
- Tries to inject WebDriver into PageObject
- HTML snippets work as well
