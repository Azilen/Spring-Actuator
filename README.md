## Spring Actuator

Spring Actuator is the spring based component, which contains features based on spring boot actuator.
It helps to monitor and manage application's health and provide features such as status of application, disk information, database status and other statistics. This information can be retrieved by hitting certain endpoints. Only two end points health metrics have been implemented recently by us.

- **P.S. Spring Actuator Version :- 0.1**
- **P.S. Spring Version :- 4.3.4.RELEASE**

### What is Spring Boot Actuator?

To help monitor and manage your application when it is pushed to production, a number of additional features have been included in Spring Boot Actuator. Production ready features to monitor health of spring boot applications have been provided by Spring Boot Actuator.

Some reference links for spring boot actuator:
- [http://www.tutorialspoint.com/articles/spring-boot-actuator-a-production-grade-feature-in-spring-boot](http://www.tutorialspoint.com/articles/spring-boot-actuator-a-production-grade-feature-in-spring-boot)
- [http://javabeat.net/spring-boot-actuator/](http://javabeat.net/spring-boot-actuator/)
- [https://spring.io/guides/gs/actuator-service/](https://spring.io/guides/gs/actuator-service/)

### Steps to start the Spring Actuator project

- Install Install MongoDB/Cassandra in your machine and start it as windows service.
- Add spring-actuator-classes as a dependency in the pom.xml of the Spring Application which is to be monitored.
- Add the actuator package (i.e. com.azilen.*) in the component scan of the Spring Application which is to be monitored.For e.g:- <context:component-scan base-package=”com.demo.*,com.azilen.*”/> where com.demo.* is the base package of the Spring Application which is to be monitored using spring actuator.
- Add Spring-admin.properties file in the classpath of the Spring Application which is to be monitored, so if it is following the maven webapp project structure,it should be added in the resources folder.
- Add spring-actuator-parent in the <parent></parent> tag in the pom.xml in the Spring Application which is to be monitored,so that the required dependencies are added.

- As of now we have developed the spring-actuator keeping the following versions
    - Spring to be 4.3.4.RELEASE
    - spring-data-mongodb to be 1.9.4.RELEASE
    - mongo-java-driver to be 3.2.2
    - jackson dependencies to be 2.7.3
    - spring-data-couchbase to be 2.1.4.RELEASE
    - java-client to be 2.2.8
    - spring-data-cassandra to be 1.5.1.RELEASE

- Copy war file and start the tomcat server.

- Now our endpoints like health, metrics will be available.

[http://localhost:8080/SpringMVCMongoDBActuator/health](http://localhost:8080/SpringMVCMongoDBActuator/health)

```json
{
  "status": "UP",
  "diskSpace": {
    "status": "UP",
    "total": 493767094272,
    "free": 388059553792,
    "threshold": 10485760
  },
  "mongo": {
    "status": "UP",
    "version": "3.0.2"
  }
}
```

[http://localhost:8080/SpringMVCMongoDBActuator/metrics](http://localhost:8080/SpringMVCMongoDBActuator/metrics)

```json
{
  "mem": 916590,
  "mem.free": 392988,
  "processors": 8,
  "instance.uptime": 154170,
  "uptime": 167724,
  "systemload.average": -1.0,
  "heap.committed": 817664,
  "heap.init": 131072,
  "heap.used": 424675,
  "heap": 1844224,
  "nonheap.committed": 100800,
  "nonheap.init": 2496,
  "nonheap.used": 98926,
  "nonheap": 0,
  "threads.peak": 49,
  "threads.daemon": 33,
  "threads.totalStarted": 59,
  "threads": 49,
  "classes": 12175,
  "classes.loaded": 12175,
  "classes.unloaded": 0,
  "gc.ps_scavenge.count": 11,
  "gc.ps_scavenge.time": 344,
  "gc.ps_marksweep.count": 3,
  "gc.ps_marksweep.time": 225,
  "httpsessions.max": -1,
  "httpsessions.active": 0
}
```

[http://localhost:8080/SpringMVCCassandraActuator/health](http://localhost:8080/SpringMVCCassandraActuator/health)

```json
{
  "status": "UP",
  "diskSpace": {
    "status": "UP",
    "total": 493767094272,
    "free": 388059525120,
    "threshold": 10485760
  },
  "cassandra": {
    "status": "UP",
    "version": "3.9.0"
  }
}```

[http://localhost:8080/SpringMVCCassandraActuator/metrics](http://localhost:8080/SpringMVCCassandraActuator/metrics)

```json
{
  "mem": 918075,
  "mem.free": 375248,
  "processors": 8,
  "instance.uptime": 316798,
  "uptime": 325635,
  "systemload.average": -1.0,
  "heap.committed": 817664,
  "heap.init": 131072,
  "heap.used": 442415,
  "heap": 1844224,
  "nonheap.committed": 102272,
  "nonheap.init": 2496,
  "nonheap.used": 100411,
  "nonheap": 0,
  "threads.peak": 49,
  "threads.daemon": 33,
  "threads.totalStarted": 67,
  "threads": 49,
  "classes": 12178,
  "classes.loaded": 12178,
  "classes.unloaded": 0,
  "gc.ps_scavenge.count": 11,
  "gc.ps_scavenge.time": 344,
  "gc.ps_marksweep.count": 3,
  "gc.ps_marksweep.time": 225,
  "httpsessions.max": -1,
  "httpsessions.active": 0
}
```

These end points have been integrated with spring boot admin UI server. Spring admin server needs to be started to view admin server. Automatic registration of application will be done to spring admin server once the admin server is running the Spring Actuator sample.
[http://localhost:8081/](http://localhost:8081/)

- Once you click on the details link, Health and Metrics details can be viewed.

![SpringAdmin](http://www.azilen.com/blog/wp-content/uploads/2017/02/Spring-Boot-Admin.png)

- When you click on the details button, metrics details can be viewed

![SpringAdminDetails](http://www.azilen.com/blog/wp-content/uploads/2017/02/Spring-Boot-Admin-Details.png)
