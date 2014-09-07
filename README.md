PageObjectEvaluator
===================

Allows evaluating PageObjects ([1][], [2][]) without starting a full Selenium test run, just feed it some HTML.

- Evaluates @FindBy* by calling PageFactory.initElements()
- Tries to inject WebDriver into PageObject
- HTML snippets work as well

[1]: https://code.google.com/p/selenium/wiki/PageObjects
[2]: http://martinfowler.com/bliki/PageObject.html

![Screenshot](/doc/screenshot.png)
