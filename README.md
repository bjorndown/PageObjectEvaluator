PageObjectEvaluator
===================
Allows evaluating [PageObjects](https://code.google.com/p/selenium/wiki/PageObjects) without starting a full Selenium test run, just feed it some HTML.

- Evaluates @FindBy*
- Tries to inject WebDriver into PageObject, if there is such a field
- Executes all no-args methods and prints their output in the console
- HTML snippets work as well

![Screenshot](/doc/screenshot.png)

How to install
===============

- Install using IntelliJ's plugin registry
- [Download](poe.zip) plugin ZIP from github
- Download from [plugins.jetbrains.com](http://plugins.jetbrains.com/plugin/7553)

