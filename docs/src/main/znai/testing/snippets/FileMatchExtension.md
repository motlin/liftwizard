```java
public class ExampleTest
{
    @RegisterExtension
    private final FileMatchExtension fileMatchExtension = new FileMatchExtension(this.getClass());

    @Test
    public void smokeTest()
    {
        String resourceClassPathLocation = this.getClass().getSimpleName() + ".txt";
        this.fileMatchExtension.assertFileContents(resourceClassPathLocation, "test content");
    }
}
```

