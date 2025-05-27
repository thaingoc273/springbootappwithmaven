## How to setup logging in Springboot

- Add 3 dependencies to springboot
```
		<!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-api -->
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>2.0.17</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api -->
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-api</artifactId>
			<version>2.24.3</version>
		</dependency>

		<!-- https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core -->
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-core</artifactId>
			<version>2.24.3</version>
		</dependency>
```

- Create log4j2.xml for tracking log
```
    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN">
        <Appenders>
            <Console name="console" target="SYSTEM_OUT">
                <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] %c{1} - %msg%n"/>
            </Console>
            <RollingFile name="RollingFile" fileName="logs/app.log"
                     filePattern="logs/app-%d{yyyy-MM-dd}.log.gz">
                <PatternLayout>
                    <Pattern>%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n</Pattern>
                </PatternLayout>
                <Policies>
                    <TimeBasedTriggeringPolicy/>
                </Policies>
            </RollingFile>
        </Appenders>
        <Loggers>
            <Root level="debug">
                <AppenderRef ref="console"/>
            </Root>
        </Loggers>
    </Configuration>
```