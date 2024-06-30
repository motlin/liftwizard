Dropwizard comes with support for dynamic configuration of [log filters](https://www.dropwizard.io/en/latest/manual/core.html#logging-filters). However, it ships with just a single filter, the [UriFilterFactory](https://www.dropwizard.io/en/latest/manual/core.html#filtering-request-logs-for-a-specific-uri).

> One can create logging filters that will intercept log statements before they are written and decide if they’re allowed. Log filters can work on both regular statements and request log statements.

Liftwizard provides an improved `RequestUrlFilterFactory` for request logs and `JaninoFilterFactory` for plain logs.

## RequestUrlFilterFactory

`RequestUrlFilterFactory` is an improved version of `UriFilterFactory`. It can filter access logs that do or don't match a list of urls.

To use it, add a dependency on `liftwizard-config-logging-filter-requesturl`. Then add a filter factory to your config with type `url` and a list of `urls` to include or exclude. The default value of `onMatch` is `ch.qos.logback.core.spi.FilterReply.DENY`.

```json
{
  "requestLog": {
    "appenders": [
      {
        "type": "console",
        "filterFactories": [
          {
            "type": "url",
            "onMatch": "DENY",
            "urls": [
              "/icons/",
              "/static/",
              "/manifest.json",
              "/assets-manifest.json",
              "/favicon.ico",
              "/service-worker.js"
            ]
          }
        ]
      }
    ]
  }
}
```

## JaninoFilterFactory

`JaninoFilterFactory` allows you to specify the filter condition in a snippet of Java code that gets compiled with [Janino](https://janino-compiler.github.io/janino/).

To use it, add a dependency on `liftwizard-config-logging-filter-janino`. Then add a filter factory to your config with type `janino` and a `javaExpression` that evaluates to a boolean. The default value of `onMatch` is `ch.qos.logback.core.spi.FilterReply.DENY`.

```json5
{
  "logging": {
    "level": "DEBUG",
    "appenders": [
      {
        "type": "console",
        "timeZone": "${LOGGING_TIMEZONE:-system}",
        "logFormat": "%highlight(%-5level) %cyan(%date{HH:mm:ss.SSS, %dwTimeZone}) %gray(\\(%file:%line\\)) [%white(%thread)] %blue(%marker) {%magenta(%mdc)} %green(%logger): %message%n%rootException",
        "filterFactories": [
          {
            "type": "janino",
            "javaExpression": "logger.equals(\"io.liftwizard.logging.p6spy.P6SpySlf4jLogger\") && mdc.get(\"liftwizard.bundle\").equals(\"DdlExecutorBundle\")",
            "onMatch": "DENY"
          }
        ]
      }
    ]
  }
}
```

