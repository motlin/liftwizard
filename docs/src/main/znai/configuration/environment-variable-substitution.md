The `EnvironmentConfigBundle` supports environment variable substitution inside Dropwizard configuration files.

# in example applications

In the example applications, environment variable substitution is used for `defaultName`.

```yaml
template: Hello, %s!
defaultName: ${DW_DEFAULT_NAME:-Stranger}
```

We can see this in action by running the `render` command, with and without the environment variable set.

```bash
$ java -jar target/liftwizard-example-0.1.0.jar render example.yml --include-default
INFO  [2020-05-02 03:07:41,910] com.example.helloworld.cli.RenderCommand: DEFAULT => Hello, Stranger!
$ DW_DEFAULT_NAME=EnvSubstitution java -jar target/liftwizard-example-0.1.0.jar render example.yml --include-default
INFO  [2020-05-02 03:08:05,685] com.example.helloworld.cli.RenderCommand: DEFAULT => Hello, EnvSubstitution!
```

# in dropwizard-example

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    // Enable variable substitution with environment variables
    bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                    bootstrap.getConfigurationSourceProvider(),
                    new EnvironmentVariableSubstitutor(false)
            )
    );
 
    // ...
}
```

# in liftwizard-example

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    bootstrap.addBundle(new EnvironmentConfigBundle());
    // ...
}
```

`EnvironmentConfigBundle` lives in the `liftwizard-bundle-environment-config` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-bundle-environment-config</artifactId>
</dependency>
```

