`LiftwizardAsyncDataFetcher` is an enhanced alternative to `AsyncDataFetcher` from [GraphQL Java](https://www.graphql-java.com/).

Both have the ability to wrap a synchronous `DataFetcher` together with an `Executor`, and return `CompleteableFuture`s that execute on the `Executor`. `LiftwizardAsyncDataFetcher` also copies slf4j's [Mapped Diagnostic Context](http://www.slf4j.org/manual.html#mdc) to the background tasks, and restores the MDC when each task completes.

```java
builder.dataFetcher(
        "fieldName",
        LiftwizardAsyncDataFetcher.async(dataFetcher, executor));
```

When using Dropwizard, the executor should come from its environment.

```java
ExecutorService executorService = environment
        .lifecycle()
        .executorService("my-data-fetcher-%d")
        .maxThreads(maxThreads)
        .build();
```

`LiftwizardAsyncDataFetcher` lives in the `liftwizard-graphql-data-fetcher-async` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-graphql-data-fetcher-async</artifactId>
</dependency>
```

