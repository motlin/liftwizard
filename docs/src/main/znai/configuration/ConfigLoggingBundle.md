The `ConfigLoggingBundle` logs the Dropwizard configuration using SLF4J. It serializes the in-memory configuration object to json and logs that json, not the contents of the original configuration file. The output contains default values set by constructors, that were not specified in the original configuration file.

To turn it on, add `ConfigLoggingBundle` to the list of registered bundles.

:include-java: com/example/helloworld/HelloWorldApplication.java {entry: "initialize", readMore: true, readMoreVisibleLines: 8}

Now `HelloWorldApplication` will log something like this on startup:

```
INFO  12:53:29 [main]  {liftwizard.priority=-8, liftwizard.bundle=ConfigLoggingBundle} io.liftwizard.dropwizard.bundle.config.logging.ConfigLoggingBundle: Inferred Dropwizard configuration:

```

```json5 {title: "Output configuration"}
{
  "template": "Hello, %s!",
  "defaultName": "Stranger",
  "configLogging": {
    "enabled": true
  },
  // ...
  "metrics": {
    "frequency": "1 minute",
    "reporters": [ ]
  }
}
```

:include-json: test-example.json5 {title: "Original configuration", include: "$['configLogging', 'template', 'metrics']"}

Note that the `metrics` section at the end was not specified in `test-example.json5`. It comes from serializing the output of `io.dropwizard.Configuration.getMetricsFactory()`.

:include-java: io/dropwizard/Configuration.java {title: "io.dropwizard.Configuration", entry: "getMetricsFactory"}

This output can be helpful for fleshing out the configuration file with default options. Including "redundant" defaults makes it easier to edit the configuration by hand. It's easier to flip a boolean flag from `false` to `true` than to first figure out where in the configuration file it belongs and the exact spelling of its key.

The `ConfigLoggingBundle` also logs the "default" configuration at the `DEBUG` level. It does this by instantiating a new copy of the configuration class using the default no-arg constructor, serializing it to json, and logging it. The default configuration output can be useful for finding redundant configuration to remove.

# Adding the dependency

`ConfigLoggingBundle` lives in the `liftwizard-bundle-logging-config` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-bundle-logging-config</artifactId>
</dependency>
```

