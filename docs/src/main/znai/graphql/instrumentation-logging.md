`LiftwizardGraphQLLoggingInstrumentation` is an implementation of `Instrumentation` from [GraphQL Java](https://www.graphql-java.com/) that adds helpful context to slf4j's [Mapped Diagnostic Context
](http://www.slf4j.org/manual.html#mdc).

For example, say that during the execution of a `DataFetcher`, we execute a database query and log its sql. It would be helpful to see the query in the context of the DataFetcher that executed it, along with the GraphQL field and its type, and the path we took through the graph on the way to this field.

This Instrumentation adds these fields to MDC, prefixed with `liftwizard.graphql`.

To turn it on, either run the entire [`LiftwizardGraphQLBundle`](graphql/bundle.md) or just add `LiftwizardGraphQLLoggingInstrumentation` to the list of instrumentations on your `GraphQLFactory`.

```java
GraphQLFactory factory = ...;

var loggingInstrumentation = new LiftwizardGraphQLLoggingInstrumentation();

List<Instrumentation> instrumentations = List.of(loggingInstrumentation);
factory.setInstrumentations(instrumentations);
```

Here's an example of what SQL logging might look like with MDC attached when formatted by the "file-logstash" appender.

```json
{
  "@timestamp": "2020-11-26T21:03:23.010-05:00",
  "@version": "1",
  "message": "select t0.key,t0.title,t0.description_markdown,t0.imgur_image_id,t0.created_by_id,t0.created_on,t0.last_updated_by_id,t0.system_from,t0.system_to from BLUEPRINT t0 inner join FIREBASE_USER t1 on t0.created_by_id = t1.user_id where  t1.system_to = '9999-12-01T18:59:00.000-0500' and substr(t1.display_name,1,9) = 'factorioi' and t0.system_to = '9999-12-01T18:59:00.000-0500'",
  "logger_name": "io.liftwizard.logging.p6spy.P6SpySlf4jLogger",
  "thread_name": "dw-35 - POST /graphql",
  "level": "INFO",
  "level_value": 20000,
  "liftwizard.graphql.executionId": "18905eb7-2d87-42b9-ad14-90856949dc4e",
  "liftwizard.graphql.field.path": "/blueprintByOperation",
  "liftwizard.graphql.field.parentType": "Query",
  "liftwizard.graphql.field.name": "blueprintByOperation",
  "liftwizard.graphql.field.type": "Blueprint",
  "liftwizard.p6spy.connectionId": 19,
  "liftwizard.p6spy.now": "2020-11-27T02:03:23.010Z",
  "liftwizard.p6spy.elapsed": 164,
  "liftwizard.p6spy.category": "statement",
  "liftwizard.p6spy.prepared": "select t0.key,t0.title,t0.description_markdown,t0.imgur_image_id,t0.created_by_id,t0.created_on,t0.last_updated_by_id,t0.system_from,t0.system_to from BLUEPRINT t0 inner join FIREBASE_USER t1 on t0.created_by_id = t1.user_id where  t1.system_to = ? and substr(t1.display_name,1,9) = ? and t0.system_to = ?",
  "liftwizard.p6spy.sql": "select t0.key,t0.title,t0.description_markdown,t0.imgur_image_id,t0.created_by_id,t0.created_on,t0.last_updated_by_id,t0.system_from,t0.system_to from BLUEPRINT t0 inner join FIREBASE_USER t1 on t0.created_by_id = t1.user_id where  t1.system_to = '9999-12-01T18:59:00.000-0500' and substr(t1.display_name,1,9) = 'factorioi' and t0.system_to = '9999-12-01T18:59:00.000-0500'",
  "liftwizard.p6spy.url": "jdbc:p6spy:h2:tcp://localhost:9096/liftwizard-app-h2;query_timeout=600000",
  "caller_class_name": "io.liftwizard.logging.p6spy.P6SpySlf4jLogger",
  "caller_method_name": "logSQL",
  "caller_file_name": "P6SpySlf4jLogger.java",
  "caller_line_number": 82
}
```

`LiftwizardGraphQLLoggingInstrumentation` lives in the `liftwizard-graphql-instrumentation-logging` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-graphql-instrumentation-logging</artifactId>
</dependency>
```

