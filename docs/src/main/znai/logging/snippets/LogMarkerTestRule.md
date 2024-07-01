`LogMarkerTestRule` is a JUnit 4 `Rule` that clears the buffer before all tests and flushes the buffer after failed tests. It does this by logging `CLEAR` and `FLUSH` markers.

```java
public class ExampleTest
{
    @Rule
    public final TestRule logMarkerTestRule = new LogMarkerTestRule();

    @Test
    public void smokeTest()
    {
        // test code
    }
}
```

`LogMarkerTestRule` lives in the `liftwizard-junit-rule-log-marker` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-junit-rule-log-marker</artifactId>
    <scope>test</scope>
</dependency>
```

