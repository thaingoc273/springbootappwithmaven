## Call API from external services [ref](https://mercyjemosop.medium.com/consume-external-api-spring-boot-7e2c684e3d00)

1. Add dependency for parsing string to Json data object

```java
		<!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
		<dependency>
			<groupId>com.google.code.gson</groupId>
			<artifactId>gson</artifactId>
			<version>2.13.1</version>
		</dependency>
```
2. Two types of Http requests in Spring boot
- WebClient
- RestTemplate [Ref](https://www.baeldung.com/rest-template): There are two ways to receive response from external API. The first one is to get plain string and then using Jackson to convert to Json note structure.
```java
RestTemplate restTemplate = new RestTemplate();
String fooResourceUrl
  = "http://localhost:8080/spring-rest/foos";
ResponseEntity<String> response
  = restTemplate.getForEntity(fooResourceUrl + "/1", String.class);
Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
```

```java
ObjectMapper mapper = new ObjectMapper();
JsonNode root = mapper.readTree(response.getBody());
JsonNode name = root.path("name");
Assertions.assertNotNull(name.asText());
```
The second construction is to create Data object transfer and map responses to ResponseEntity. Then Spring will automatically parse the string to get json format

```java
Foo foo = restTemplate
  .getForObject(fooResourceUrl + "/1", Foo.class);
Assertions.assertNotNull(foo.getName());
Assertions.assertEquals(foo.getId(), 1L);
```
Example: RestTemplate with Path variables, Request Body and Custom Headers
    - Define Request Body DTO
```java
public class PopulationRequestDto {
    private int year;
    private long population;

    // Getters and setters
}
```
    - Prepare the RestTemplate Post logic: Using `HttpHeaders` and `HttpEntity`

```java
@Service
public class PopulationApiClient {

    private final RestTemplate restTemplate;

    public PopulationApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<String> postPopulationData(Long cityId, PopulationRequestDto requestDto) {
        // Build URL with path variable
        String url = "https://api.example.com/cities/{cityId}/population";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer your_token_here");

        // Build request entity
        HttpEntity<PopulationRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        // Perform POST with path variable
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class,
                cityId // This fills the {cityId} path variable
        );
    }
}
```