```java
public class ExampleTest
{
    @Rule
    public final FileMatchRule fileMatchRule = new FileMatchRule(this.getClass());

    @Test
    public void smokeTest()
    {
        String resourceClassPathLocation = this.getClass().getSimpleName() + ".txt";
        this.fileMatchRule.assertFileContents(resourceClassPathLocation, "test content");
    }
}
```

