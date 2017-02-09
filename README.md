## Spring Actuator

Spring Actuator is the spring based component, which contains features based on spring boot actuator.
It helps to monitor and manage application's health and provide features such as status of application, disk information, database status and other statistics. This information can be retrieved by hitting certain endpoints. Only two end points health metrics have been implemented recently by us.
- **P.S. Spring Actuator Version :- 0.7.0-beta**
- **P.S. Spring Version :- 4.3.4.RELEASE**

### What is Spring Boot Actuator?

To help monitor and manage your application when it is pushed to production, a number of additional features have been included in Spring Boot Actuator. Production ready features to monitor health of spring boot applications have been provided by Spring Boot Actuator.

Some reference links for spring boot actuator:
- [http://www.tutorialspoint.com/articles/spring-boot-actuator-a-production-grade-feature-in-spring-boot](http://www.tutorialspoint.com/articles/spring-boot-actuator-a-production-grade-feature-in-spring-boot)
- [http://javabeat.net/spring-boot-actuator/](http://javabeat.net/spring-boot-actuator/)
- [https://spring.io/guides/gs/actuator-service/](https://spring.io/guides/gs/actuator-service/)

### Steps to start the Spring Actuator project

- Install MongoDB in your machine and start it as windows service.
- Download the project and create build (war) using maven or any  IDE tools (i.e. Eclipse, Netbeans etc)
- Copy war file and start the tomcat server.
- Now our endpoints like health, metrics will be available.

[http://localhost:8080/SpringActuatorSample/health](http://localhost:8080/SpringActuatorSample/health)

```json
{
"status":"UP",
"diskSpace":{"status":"UP","total":493767094272,"free":404976193536,"threshold":10485760},
"mongo":{"status":"UP","version":"3.0.2"}
}
```

[http://localhost:8080/SpringActuatorSample/metrics](http://localhost:8080/SpringActuatorSample/metrics)

```json
{
"mem":263044,
"mem.free":111634,
"processors":8,
"instance.uptime":1,
"uptime":7530,
"systemload.average":-1.0,
"heap.committed":223232,
"heap.init":131072,
"heap.used":111597,
"heap":1844224,
"nonheap.committed":41664,
"nonheap.init":2496,
"nonheap.used":39823,
"nonheap":0,
"threads.peak":13,
"threads.daemon":12,
"threads.totalStarted":13,
"threads":13,
"classes":4750,
"classes.loaded":4750,
"classes.unloaded":0,
"gc.ps_scavenge.count":9,
"gc.ps_scavenge.time":162,
"gc.ps_marksweep.count":1,
"gc.ps_marksweep.time":34,
"httpsessions.max":-1,
"httpsessions.active":0
}
```

These end points have been integrated with spring boot admin UI server. Spring admin server needs to be started to view admin server. Automatic registration of application will be done to spring admin server once the admin server is running the Spring Actuator sample.
[http://localhost:8081/](http://localhost:8081/)

- Once you click on the details link, Health and Metrics details can be viewed.

![SpringAdmin](https://github.com/Azilen/Spring-Actuator/blob/Development/SpringAdmin.png)

- When you click on the details button, metrics details can be viewed

![SpringAdminDetails](https://github.com/Azilen/Spring-Actuator/blob/Development/SpringAdminDetails.png)
