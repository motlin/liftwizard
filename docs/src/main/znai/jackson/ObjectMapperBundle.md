The `ObjectMapperBundle` configures the Jackson `ObjectMapper` used by Dropwizard for serializing and deserializing all responses, as well as for logging by bundles such as `liftwizard-bundle-logging-config`.

`ObjectMapperBundle` supports configuring pretty-printing on or off, and serialization inclusion to any value in Jackson's `JsonInclude.Include`.

`ObjectMapperBundle` also turns on all json5 features, turns on `FAIL_ON_UNKNOWN_PROPERTIES`, turns on `STRICT_DUPLICATE_DETECTION`, and turns on serialization of dates and Strings.

To turn it on, add `ObjectMapperBundle` to the list of registered bundles.

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    // JsonConfigurationFactoryFactory uses a separate ObjectMapper, and can be configured earlier
    bootstrap.setConfigurationFactoryFactory(new JsonConfigurationFactoryFactory<>());
    bootstrap.addBundle(new EnvironmentConfigBundle());
 
    bootstrap.addBundle(new ObjectMapperBundle());
 
    // ConfigLoggingBundle uses the ObjectMapper configured by ObjectMapperBundle
    bootstrap.addBundle(new ConfigLoggingBundle());
 
    // ...
}
```

You'll be able to see that `ObjectMapperBundle` is working because the output of `ConfigLoggingBundle` will now be pretty-printed by default.

# Adding the dependency

`ObjectMapperBundle` lives in the `liftwizard-bundle-object-mapper` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-bundle-object-mapper</artifactId>
</dependency>
```

