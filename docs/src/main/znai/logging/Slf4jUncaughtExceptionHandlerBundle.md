`Slf4jUncaughtExceptionHandler` is an [`UncaughtExceptionHandler`](https://docs.oracle.com/en%2Fjava%2Fjavase%2F21%2Fdocs%2Fapi%2F%2F/java.base/java/lang/Thread.UncaughtExceptionHandler.html) that logs uncaught exceptions using SLF4J.

`Slf4jUncaughtExceptionHandlerBundle` is a Dropwizard bundle that installs `Slf4jUncaughtExceptionHandler` on startup.

> When a thread is about to terminate due to an uncaught exception the Java Virtual Machine will query the thread for its UncaughtExceptionHandler using Thread.getUncaughtExceptionHandler() and will invoke the handler's uncaughtException method, passing the thread and the exception as arguments.

# The logs

When an uncaught exception is thrown, `Slf4jUncaughtExceptionHandler` logs the exception at the WARN level.

With logback configuration like this:

```xml
<appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%highlight(%-5level) %cyan(%date{HH:mm:ss.SSS, ${LOGGING_TIMEZONE}}) %gray(\(%file:%line\)) [%white(%thread)] %blue(%marker) {%magenta(%mdc)} %green(%logger): %message%n%rootException</pattern>
    </encoder>
</appender>
```

The logs look like this:

```shell
WARN  12:00:00.000 (Slf4jUncaughtExceptionHandler.java:46) [main]  {exceptionClass=io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandlerTest.RootException, liftwizard.error.message=example root, liftwizard.error.kind=io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandlerTest.RootException, threadName=main, exceptionMessage=example root, liftwizard.error.thread=main} io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandler: Exception in thread "main"
io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandlerTest$CauseException: example cause
	at io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandlerTest.testUncaughtException(Slf4jUncaughtExceptionHandlerTest.java:26) ~[test-classes/:na]
	... 68 common frames omitted
Wrapped by: io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandlerTest$RootException: example root
	at io.liftwizard.logging.slf4j.uncaught.exception.handler.Slf4jUncaughtExceptionHandlerTest.testUncaughtException(Slf4jUncaughtExceptionHandlerTest.java:27) ~[test-classes/:na]
```

# With Dropwizard

To use the exception handler with Dropwizard, add `Slf4jUncaughtExceptionHandlerBundle` to the list of registered bundles.

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap)
{
    bootstrap.addBundle(new Slf4jUncaughtExceptionHandlerBundle());
}
```

And add the dependency:

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-bundle-logging-uncaught-exception-handler</artifactId>
</dependency>
```

# Without Dropwizard

To use `Slf4jUncaughtExceptionHandler` without the bundle, create an instance and set it as the default uncaught exception handler.

```java
Thread.setDefaultUncaughtExceptionHandler(new Slf4jUncaughtExceptionHandler());
```

And add the dependency:

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-logging-uncaught-exception-handler</artifactId>
</dependency>
```

