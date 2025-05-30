## JAVA with Springboot
- Start project
     - [x] Download a project template from [spring io](https://start.spring.io/)
     - [x] Setup database with flyway using MySQL
     - [x] Add dependencies to the project
     - [x] Create docker-compose services 
- Building Rest APIs
     - [x] Building basic APIs (GET, PUT, POST, PATCH, DELETE) for `users` and `role` tables
     - [x] Check DateTimeLocal in MySQL (UTC by default)
     - [x] Change id to UUID which is commonly used in production
     - [x] Building Repository, Service and Controller layers for Rest APIs
     - [x] Construct Entity, DTO (Data Transfer Object)
     - [x] Create Exception class using ``Enum`` for handling errors
- Creating Test environment using JUnit5
     - [x] Set up H2 as database environment for testing
     - [x] Writing test for Rest APIs endpoints
     - [x] Check compatible between H2 database and MySQL
     - [x] Create full integration test       
     - [x] Swagger: for documents and APIs listing, testing
     - [x] log4j, sl4j: for debugging and visibility
     - [x] Create end point for upload excel file and save to database
     - [x] Create full integration test for upload excel file
- Need to be done
     - [x] Build Jar file using Maven plugin (in deployment)
     - [x] Read file
     - [ ] Mock test and integration test
     - [ ] Solid priciple in software engineering     
     - [ ] Create end point that calls from another API
     - [ ] Create Cron job
     - [ ] Concurrence in database
