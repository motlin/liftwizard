The `JerseyHttpLoggingBundle` is an alternative to Jersey's `LoggingFeature`. Jersey's `LoggingFeature` can be configured to log or not log bodies, but it cannot be configured to exclude headers. Since headers can include authentication tokens, you may not want to log headers, or only log those in an allow-list.

The bundle can be configured:

* include/exclude request bodies
* include/exclude response bodies
* allow-list of headers
* include/exclude the list of excluded header _names_
* the max body size before truncation

Through code, the bundle can be configured to log using different combinations of slf4j/log4j/logback with context in MDC or OpenTracing or a Map.

To turn it on, add `JerseyHttpLoggingBundle` to the list of registered bundles.

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    bootstrap.setConfigurationFactoryFactory(new JsonConfigurationFactoryFactory<>());
    bootstrap.addBundle(new EnvironmentConfigBundle());
 
    bootstrap.addBundle(new ObjectMapperBundle());
    bootstrap.addBundle(new ConfigLoggingBundle());

    StructuredArgumentsMDCLogger structuredLogger = new StructuredArgumentsMDCLogger(bootstrap.getObjectMapper());
    bootstrap.addBundle(new JerseyHttpLoggingBundle(structuredLogger));

    // ...
}
```

The bundle registers filters which gather all the arguments to log. A "logger" is passed into the constructor which abstracts over whether the logging uses logback or log4j, whether the structured arguments are converted into MDC, Markers, or a Map. The `StructuredArgumentsMDCLogger` in the example above logs using slf4j with context in MDC.

```java
var mdcLogger = new StructuredArgumentsMDCLogger(bootstrap.getObjectMapper());
var logstashLogger = new StructuredArgumentsLogstashEncoderLogger();

Consumer<StructuredArguments> structuredLogger = structuredArguments ->
{
    mdcLogger.accept(structuredArguments);
    logstashLogger.accept(structuredArguments);
};

bootstrap.addBundle(new JerseyHttpLoggingBundle(structuredLogger));
```

`JerseyHttpLoggingBundle` lives in the `liftwizard-bundle-logging-http` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-bundle-logging-http</artifactId>
</dependency>
```

In order to see the logging in action, we'll need to configure a log format that includes mdc and markers.

### test-example.json5

`src/test/resources/test-example.json5`

```json5
{
  "type": "console",
  "timeZone": "${LOGGING_TIMEZONE:-system}",
  "logFormat": "%highlight(%-5level) %cyan(%date{HH:mm:ss.SSS, %dwTimeZone}) %gray(\\(%file:%line\\)) [%white(%thread)] %blue(%marker) {%magenta(%mdc)} %green(%logger): %message%n%rootException",
  "includeCallerData": true,
}
```

Next, lets turn on all the basic filters and see how they change what gets logged.

## Logging output

We can rerun `IntegrationTest` and see the new logs in action.

```console {title: "Logging output (newlines added for clarity)"}
DEBUG 13:21:49 [dw-249] io.liftwizard.servlet.logging.mdc.StructuredArgumentsMDCLogger: Response sent <> <
response.http.elapsedNanos=1000000000,
request.http.method=GET,
request.http.parameters.query.name=Dr. IntegrationTest,
request.http.path.full=/hello-world,
request.http.path.absolute=http://localhost:63842/hello-world,
request.http.client.port=63855,
request.http.headers.User-Agent=Jersey/2.25.1 (HttpUrlConnection 17.0.2),
request.http.server.port=63842,
request.http.client.host=127.0.0.1,
request.resourceClass=com.example.helloworld.resources.HelloWorldResource,
request.http.path.template=/hello-world,
request.http.server.name=localhost,
request.http.headers.Host=localhost:63842,
response.http.headers.Content-Type=application/json,
response.http.contentType=application/json,
response.http.entityType=com.example.helloworld.api.Saying,
response.http.status.code=200,
request.http.client.address=127.0.0.1,
request.resourceMethod=sayHello,
response.http.status.phrase=OK,
response.http.body={
  "id" : 1,
  "content" : "Hello, Dr. IntegrationTest!"
},
response.http.contentLength=59,
request.http.server.scheme=http,
response.http.status.status=OK,
response.http.status.family=SUCCESSFUL>
```

## Logstash encoder

`liftwizard-config-logging-logstash-file` is a Dropwizard `AppenderFactory`. It sets up a file appender that logs one json object per log statement. The json is formatted by [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder) and is ready to be parsed by logstash.

Let's add the logstash-file appender to the list of configured appenders.

### test-example.json5

`src/test/resources/test-example.json5`

```json5
{
  // ...
  "logging": {
    "level": "DEBUG",
    "appenders": [
      {
        "type": "console",
        "timeZone": "${LOGGING_TIMEZONE:-system}",
        "logFormat": "%highlight(%-5level) %cyan(%date{HH:mm:ss.SSS, %dwTimeZone}) %gray(\\(%file:%line\\)) [%white(%thread)] %blue(%marker) {%magenta(%mdc)} %green(%logger): %message%n%rootException",
        "includeCallerData": true,
      },
      {
        "type"                      : "file-logstash",
        "currentLogFilename"        : "./logs/logstash.jsonl",
        "archivedLogFilenamePattern": "./logs/logstash-%d.jsonl",
        "includeCallerData"         : true,
        "encoder": {
          "includeContext": true,
          "includeMdc": true,
          "includeStructuredArguments": true,
          "includedNonStructuredArguments": true,
          "includeTags": true,
          "prettyPrint": false,
        }
      }
    ]
  },
  // ...
}
```

### logstash.jsonl

`logs/logstash.jsonl` snippet

```json
{
  "@timestamp": "1999-12-31T23:59:59.000-00:00",
  "@version": "1",
  "message": "Response sent",
  "logger_name": "io.liftwizard.servlet.logging.mdc.StructuredArgumentsMDCLogger",
  "thread_name": "dw-249",
  "level": "DEBUG",
  "level_value": 10000,
  "response.http.elapsedNanos": "1000000000",
  "request.http.method": "GET",
  "request.http.parameters.query.name": "Dr. IntegrationTest",
  "request.http.path.full": "/hello-world",
  "request.http.path.absolute": "http://localhost:63842/hello-world",
  "request.http.client.port": "63855",
  "request.http.headers.User-Agent": "Jersey/2.25.1 (HttpUrlConnection 17.0.2)",
  "request.http.server.port": "63842",
  "request.http.client.host": "127.0.0.1",
  "request.resourceClass": "com.example.helloworld.resources.HelloWorldResource",
  "request.http.path.template": "/hello-world",
  "request.http.server.name": "localhost",
  "request.http.headers.Host": "localhost:63842",
  "response.http.headers.Content-Type": "application/json",
  "response.http.contentType": "application/json",
  "response.http.entityType": "com.example.helloworld.api.Saying",
  "response.http.status.code": "200",
  "request.http.client.address": "127.0.0.1",
  "request.resourceMethod": "sayHello",
  "response.http.status.phrase": "OK",
  "response.http.body": "{\n  \"id\" : 1,\n  \"content\" : \"Hello, Dr. IntegrationTest!\"\n}",
  "response.http.contentLength": "59",
  "request.http.server.scheme": "http",
  "response.http.status.status": "OK",
  "response.http.status.family": "SUCCESSFUL",
  "caller_class_name": "io.liftwizard.servlet.logging.mdc.StructuredArgumentsMDCLogger",
  "caller_method_name": "accept",
  "caller_file_name": "StructuredArgumentsMDCLogger.java",
  "caller_line_number": 56
}
```

