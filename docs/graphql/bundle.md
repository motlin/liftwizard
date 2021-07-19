The `LiftwizardGraphQLBundle` extends `com.smoketurner.dropwizard.graphql.GraphQLBundle`. 

The bundle registers [the GraphIQL UI](https://github.com/graphql/graphiql) at `/graphiql` and [the GraphQL Playground UI](https://github.com/graphql/graphql-playground) at `/graphql-playground`, by delegating to `AssetsBundle`. This overrides the behavior of the smoketurner bundle, which registers just one UI at `/` (graphiql in older versions, and graphql-playground in newer versions).

The bundle also registers two instrumentations for logging and metrics. If you choose not to use the bundle, you can still register the instrumentations separately.


To turn it on, add `LiftwizardGraphQLBundle` to the list of registered bundles.
 
```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap)
{
    bootstrap.setConfigurationFactoryFactory(new JsonConfigurationFactoryFactory<>());
    bootstrap.addBundle(new EnvironmentConfigBundle());

    bootstrap.addBundle(new ObjectMapperBundle());
    bootstrap.addBundle(new ConfigLoggingBundle());

    bootstrap.addBundle(new JerseyHttpLoggingBundle());

    bootstrap.addBundle(new LiftwizardGraphQLBundle<>(
            builder ->
            {
                // TODO: Set up GraphQL wiring
                // builder.scalar(...);
                // builder.type(...);
            })); 

    // ...
}
```

`LiftwizardGraphQLBundle` lives in the `liftwizard-bundle-graphql` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-bundle-graphql</artifactId>
</dependency>
```
