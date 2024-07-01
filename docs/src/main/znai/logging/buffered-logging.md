In unit tests, it can be useful to suppress all logging for successful tests, but still log everything when tests fail.

In order to accomplish this, we need to buffer all logging before we know the result of the test, and then flush or clear the buffer once we know the outcome.

## BufferedAppender

`BufferedAppender` is the logback appender that buffers all logging until it receives a `CLEAR` or `FLUSH` marker.

You can use  directly in logback configuration. It requires a delegate appender for flushing, declared using an `appender-ref`.

:include-xml: logback-test.xml {title: "logback-test.xml"}

`BufferedAppender` lives in the `liftwizard-logging-buffered-appender` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-logging-buffered-appender</artifactId>
    <scope>test</scope>
</dependency>
```

## Log Markers

We must log `CLEAR` and `FLUSH` markers to instruct `BufferedAppender` to clear or flush its logs. If you are using JUnit 4 or 5, you can use the included Rule or Extension to log these markers automatically.

```tabs
"JUnit 4": :include-markdown: snippets/LogMarkerTestRule.md
"JUnit 5": :include-markdown: snippets/LogMarkerTestExtension.md
```

## BufferedAppenderFactory

The `BufferedAppenderFactory` allows you to use an appender with the type `buffered` where you would otherwise use `console` in your Dropwizard configuration.

```json5
"logging": {
  "level": "DEBUG",
  "appenders": [
    {
      "type": "buffered",
      "timeZone": "${LOGGING_TIMEZONE:-system}",
      "logFormat": "%highlight(%-5level) %cyan(%date{HH:mm:ss.SSS, %dwTimeZone}) %gray(\\(%file:%line\\)) [%white(%thread)] %blue(%marker) {%magenta(%mdc)} %green(%logger): %message%n%rootException",
      "includeCallerData": true,
    },
  ]
}
```

`BufferedAppenderFactory` lives in the `liftwizard-config-logging-buffered` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-config-logging-buffered</artifactId>
    <scope>test</scope>
</dependency>
```

Note: `BufferedAppenderFactory` is primarily useful for tests that use [Dropwizard's JUnit 4 Rule](https://www.dropwizard.io/en/release-2.1.x/manual/testing.html#junit-4) `DropwizardAppRule` or [Dropwizard's JUnit 5 Extension](https://www.dropwizard.io/en/release-2.1.x/manual/testing.html#junit-5) `DropwizardAppExtension`.

```java
private final TestRule logMarkerTestRule = new LogMarkerTestRule();

private final DropwizardAppRule<HelloWorldConfiguration> dropwizardAppRule = new DropwizardAppRule<>(
        HelloWorldApplication.class,
        ResourceHelpers.resourceFilePath("test-example.json5"));

@Rule
public final RuleChain ruleChain = RuleChain
        .outerRule(this.dropwizardAppRule)
        .around(this.logMarkerTestRule);
```

Note: `LogMarkerTestRule` needs to be an inner rule, with any other rules that tear down logging outer to it.
