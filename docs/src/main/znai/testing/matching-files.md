Liftwizard includes utilities for asserting that a string equals the contents of a file.

If your code has changed enough, it can be more convenient to re-record the test resource files, and review the changes using `git diff` rather than the test assertion errors. To enable re-record mode, set the environment variable `LIFTWIZARD_FILE_MATCH_RULE_RERECORD` to `true`.

The setup is different for the JUnit 4 Rule and JUnit 5 Extension. After setup, both have the same API.

```java
assertFileContents(resourceClassPathLocation, actualString);
```

If the file does not exist, or the contents do not match, an assertion error is added to an [ErrorCollector](https://junit.org/junit4/javadoc/4.12/org/junit/rules/ErrorCollector.html). If the ErrorCollector contains any errors, the test fails at the end with all expected/actual pairs reported together.

If `LIFTWIZARD_FILE_MATCH_RULE_RERECORD` is set to `true`, `assertFileContents` will not emit any `AssertionErrors`.

```tabs
"JUnit 4": :include-markdown: snippets/FileMatchRule.md
"JUnit 5": :include-markdown: snippets/FileMatchExtension.md
```

