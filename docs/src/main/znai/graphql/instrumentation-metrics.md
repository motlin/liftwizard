`LiftwizardGraphQLMetricsInstrumentation` is an implementation of `Instrumentation` from [GraphQL Java](https://www.graphql-java.com/) that registers [performance metrics](https://metrics.dropwizard.io/) about data fetching with Dropwizard's MetricsRegistry.

To turn it on, either run the entire [`LiftwizardGraphQLBundle`](graphql/bundle.md) or just add `LiftwizardGraphQLMetricsInstrumentation` to the list of instrumentations on your `GraphQLFactory`.

```java
GraphQLFactory factory = ...;

Clock clock = Clock.systemUTC();

var metricsInstrumentation = new LiftwizardGraphQLMetricsInstrumentation(this.metricRegistry, clock);
var loggingInstrumentation = new LiftwizardGraphQLLoggingInstrumentation();

List<Instrumentation> instrumentations = List.of(metricsInstrumentation, loggingInstrumentation);
factory.setInstrumentations(instrumentations);
```

## Annotations

Next, annotate the DataFetchers that you want to monitor with `@Timed`, `@Metered`, and/or `@ExceptionMetered`. You can annotate either the `get()` method, or the entire fetcher class.

## Timers

`@Timed` adds three timers:
* {DataFetcher's fully-qualified class name}.get.sync
* liftwizard.graphql.field.{GraphQL Class}.{GraphQL field}.sync
* liftwizard.graphql.path.{path}.sync

All three timers track the number of times each DataFetcher is called, and the amount of time spent in the get() method.

Although the timers measure the same thing, they may not have identical values. This would happen if the same DataFetcher is wired to multiple fields, or is reached by multiple paths through the graph.

If your DataFetcher returns [`CompleteableFuture`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletableFuture.html), you'll get three additional timers, with names ending in "async" instead of "sync". Rather than track the amount of time spent in `get()`, these timers will track the amount of time until the `CompleteableFutures` complete.

## Meters

Timers *are* meters, so if you want to know the number of times a fetcher is called, annotate them with *@Timer*.

If you annotate your DataFetcher with `@Metered`, the Intrumentation will add meters that track *the number of items returned* by the DataFetcher. If the `DataFetcher` returns a `Collection` or `CompleteableFuture<Collection>`, the meter will increment by the size of the Collection.

## ExceptionMeters

`@ExceptionMetered` adds meters that track the number of times the DataFetcher throws uncaught exceptions, plus the number of CompleteableFutures they return that complete exceptionally. The meters have the same names as the timers, but with the suffix "exceptions":
* {DataFetcher's fully-qualified class name}.get.exceptions
* liftwizard.graphql.field.{GraphQL Class}.{GraphQL field}.exceptions
* liftwizard.graphql.path.{path}.exceptions

`LiftwizardGraphQLMetricsInstrumentation` lives in the `liftwizard-graphql-instrumentation-metrics` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-graphql-instrumentation-metrics</artifactId>
</dependency>
```

