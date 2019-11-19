# PageObjectEvaluator
Allows evaluating [PageObjects](https://github.com/SeleniumHQ/selenium/wiki/PageObjects) without starting a full Selenium test run, just feed it some HTML.

- Evaluates @FindBy*
- Tries to inject WebDriver into PageObject, if there is such a field
- Executes all no-args methods and prints their output in the console
- HTML snippets work as well

![Screenshot](/doc/screenshot.png)

## How to install

- Install using IntelliJ's plugin registry
- Download from [plugins.jetbrains.com](http://plugins.jetbrains.com/plugin/7553)
- Build it yourself, see below

## How to build and run locally
- You need to use Java 8 for building, see Troubleshooting section for reason
- See [gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin) for more targets

### Build
```
./gradlew build
```

### Run tests
```
./gradlew test
```

### Verify plugin archive and folder structure
```
./gradlew verifyPlugin
```

### Start an IntelliJ instance with the plugin installed
```
./gradlew runIde
```

## Troubleshooting

### Build-time

#### Class not found: JPanel
If you encounter `Class not found: javax.swing.JPanel`, make sure you are building
the plugin with Java 8. See https://youtrack.jetbrains.com/issue/IDEA-191412

### Run-time

#### Class not found: Webdriver
If you encounter `java.lang.NoClassDefFoundError: org/openqa/selenium/WebDriver`,
make sure you have the correct module selected in the run configuration:
![Run config module selection](/doc/troubleshooting-run-config-module-selection.png)
Note: This example is taken from a gradle project