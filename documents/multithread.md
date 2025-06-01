## Multithread in Springboot
1. Configure the multithread environment
```java
@Configuration
public class ThreadPoolConfig {
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("My Thread-");
        executor.initialize();
        return executor;
    }
}
```

2. Put the method in `async` mode for multithread implementation in `AsyncUserService` (Note that thread is only initiate if annotation `Async` is created from a different file)
```java
    @Async("threadPoolTaskExecutor")
    public void saveUsers(User user) {
        userRepository.save(user);
    }
```

3. Two ways to implement `@Async` annotation
- Create async method from different class
- Create proxy by self-inject
```java
@Service
public class OrderService {

    @Autowired
    private OrderService selfProxy;

    @Transactional
    public void placeOrder() {
        // works
    }

    public void process() {
        selfProxy.placeOrder(); // proxy used, transaction applies
    }
}

``` 
4. Some reference
- [Medium blog](https://medium.com/@nidhiupreti99/introduction-to-multithreading-java-spring-boot-b4930b73f302)