`LogMarkerTestExtension` is a JUnit 5 `Extension` that clears the buffer before all tests and flushes the buffer after failed tests. It does this by logging `CLEAR` and `FLUSH` markers.

```java
@ExtendWith(LogMarkerTestExtension.class)
public class ExampleTest
{
    @Test
    public void smokeTest()
    {
        // test code
    }
}
```

`LogMarkerTestExtension` lives in the `liftwizard-junit-extension-log-marker` module.

```xml
<dependency>
    <groupId>io.liftwizard</groupId>
    <artifactId>liftwizard-junit-extension-log-marker</artifactId>
    <scope>test</scope>
</dependency>
```

