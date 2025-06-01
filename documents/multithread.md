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

2. Put the method in `async` mode for multithread implementation
```java
    @Async("threadPoolTaskExecutor")
    public void saveUsers(User user) {
        userRepository.save(user);
    }
```
3. Some reference
- [Medium blog](https://medium.com/@nidhiupreti99/introduction-to-multithreading-java-spring-boot-b4930b73f302)