## Mock annotation [reference](https://www.baeldung.com/mockito-annotations)
1. Enable Mockito Annotation
- Annotate the JUnit test with the MockitoJUnitRunner
```java
@ExtendWith(MockitoExtension.class)
public class MockitoAnnotationUnitTest {
    ...
}
```
- Open Mockito grammatically by invoking `MockitoAnnotations.openMocks()`
```java
@BeforeEach
public void init() {
    MockitoAnnotations.openMocks(this);
}
```
- Make rules for Mockito
```java
public class MockitoAnnotationsInitWithMockitoJUnitRuleUnitTest {

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();

    ...
}
```
2. Mock annotation
- `@Mock`: To create and inject mocked instances without to call Mockito.mock
```java
@Test
public void whenNotUseMockAnnotation_thenCorrect() {
    List mockList = Mockito.mock(ArrayList.class);
    
    mockList.add("one");
    Mockito.verify(mockList).add("one");
    assertEquals(0, mockList.size());

    Mockito.when(mockList.size()).thenReturn(100);
    assertEquals(100, mockList.size());
}
```
- `@DoNotMock`: Indicate that a specific type should not be mocked during testing
```java
import org.mockito.exceptions.misusing.DoNotMock;

@DoNotMock(reason = "Use a real instance instead")
public abstract class NotToMock {
    // Class implementation
}
```
- `@Spy`: spy on an existing instance
```java
@Test
public void whenNotUseSpyAnnotation_thenCorrect() {
    List<String> spyList = Mockito.spy(new ArrayList<String>());
    
    spyList.add("one");
    spyList.add("two");

    Mockito.verify(spyList).add("one");
    Mockito.verify(spyList).add("two");

    assertEquals(2, spyList.size());

    Mockito.doReturn(100).when(spyList).size();
    assertEquals(100, spyList.size());
}
```
- `@Captor`: to create `ArgumentCaptor` instance
```java 
@Test
public void whenNotUseCaptorAnnotation_thenCorrect() {
    List mockList = Mockito.mock(List.class);
    ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);

    mockList.add("one");
    Mockito.verify(mockList).add(arg.capture());

    assertEquals("one", arg.getValue());
}
```
Make use of `@Captor` to create an ArgumentCaptor instance [ref](https://www.baeldung.com/mockito-argumentcaptor)

```java
@Mock
List mockedList;

@Captor 
ArgumentCaptor argCaptor;

@Test
public void whenUseCaptorAnnotation_thenTheSame() {
    mockedList.add("one");
    Mockito.verify(mockedList).add(argCaptor.capture());

    assertEquals("one", argCaptor.getValue());
}
```
```java
@Test
void whenDoesSupportHtml_expectHTMLEmailFormat() {
    String to = "info@baeldung.com";
    String subject = "Using ArgumentCaptor";
    String body = "Hey, let'use ArgumentCaptor";

    emailService.send(to, subject, body, true);

    verify(platform).deliver(emailCaptor.capture());
    Email value = emailCaptor.getValue();
    assertThat(value.getFormat()).isEqualTo(Format.HTML);
}
```
- `@InjectMocks` inject mock fields into the tested object automatically