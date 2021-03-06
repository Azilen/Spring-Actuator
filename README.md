## Spring Actuator

Spring Actuator is the spring based component, which contains features based on spring boot actuator.
It helps to monitor and manage application's health and provide features such as status of application, disk information, database status and other statistics. This information can be retrieved by hitting certain endpoints. Only two end points health metrics have been implemented recently by us.

- **Health Endpoint**  : health endpoint provides basic application health information.
- **Metrics Endpoint** : Shows ‘metrics’ information for the spring application.

### What is Spring Boot Actuator?

Spring boot Actuator module in spring boot framework helps application developers to implement the features like metrics, health check, security, etc. with minimal effort.

Some reference links for spring boot actuator:
- [http://www.tutorialspoint.com/articles/spring-boot-actuator-a-production-grade-feature-in-spring-boot](http://www.tutorialspoint.com/articles/spring-boot-actuator-a-production-grade-feature-in-spring-boot)
- [http://javabeat.net/spring-boot-actuator/](http://javabeat.net/spring-boot-actuator/)
- [https://spring.io/guides/gs/actuator-service/](https://spring.io/guides/gs/actuator-service/)

### Spring Actuator Version : 1.0

- ## Prerequisite

1. Your project must be in Spring 4.3.4 RELEASE or above.
2. Your project must use MongoDB or Cassandra database.

- ## Integration with Spring application
1. Download **springactuator** library and install it in your local repository.<br />
    ``` mvn clean install ```
2. Download **spring-actuator-parent** library and install it in your local repository.<br />
    ``` mvn clean install ```
3. Add spring-actuator-parent in the <parent></parent> tag in the pom.xml in your Spring Application which is to be monitored,so that the required dependencies will be added.
    ``` xml
    <parent>
		<groupId>com.azilen.spring</groupId>
		<artifactId>spring-actuator-parent</artifactId>
		<version>1.0</version>
    </parent>
    ```
4. Add springactuator dependency in the pom.xml in your Spring Application which is to be monitored,so that the required dependencies will be added.
    ``` xml
    <dependency>
		<groupId>com.azilen.spring</groupId>
		<artifactId>springactuator</artifactId>
		<version>1.0</version>
    </dependency>
    ```
5. You will require to scan **springactuator** packages in your application.

6. If your project has xml based configuration add below line

    ``` xml 
    <context:component-scan base-package="com.azilen.spring.*" /> 
    ```

7. Add Spring-admin.properties file in the classpath of the Spring Application which is to be monitored. It should be added in the resources folder.

8. You can configure properties as below
    - server.displayName=SpringMVCMongoDBActuator
    - spring.boot.admin.url=http://localhost:8081
    - info.app.version=This is SpringMVCMongoDBActuator app version

9. We have developed the spring-actuator keeping the following versions
    - Spring 4.3.4.RELEASE
    - spring-data-mongodb to be 1.9.4.RELEASE
    - mongo-java-driver to be 3.2.2
    - jackson dependencies to be 2.7.3
    - spring-data-couchbase to be 2.1.4.RELEASE
    - java-client to be 2.2.8
    - spring-data-cassandra to be 1.5.1.RELEASE

10. build war file of your spring application.

11. Copy war file and start the tomcat server.

12. Now our endpoints like health, metrics will be available.

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
}
```

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

13. These end points have been integrated with spring boot admin UI server. Spring admin server needs to be started to view admin server. Automatic registration of application will be done to spring admin server once the admin server is running the Spring Actuator sample.

	Spring Admin UI server : [http://localhost:8081/](http://localhost:8081/)

14. Once you click on the details link, Health and Metrics details can be viewed.

![SpringAdmin](http://www.azilen.com/blog/wp-content/uploads/2017/06/1.png)

15. When you click on the details button, metrics details can be viewed

![SpringAdminDetails](http://www.azilen.com/blog/wp-content/uploads/2017/06/2.png)
