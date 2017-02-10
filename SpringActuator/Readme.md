##Spring Actuator

Spring Actuator includes features where you can monitor and manage your spring application. Health and metrics gathering can be automatically applied to your application.

###Steps:
- Install MongoDB in your machine as start it as windows service.
- SpringActuator is maven based spring web application. You can download source code in your local and build the project.
- You can use maven from terminal to build the SpringActuator application.
  `mvn clean compile package install`
- Above command will download all required dependenacies from maven centeral repository and will create .war file inside "/target" folder.
- Now inside "/target" folder SpringActuator.war will be generated.
- You just need to copy this SpringActuator.war to "webapps" folder of your Apache tomcat and start tomcat server.
- Now try to hit below url to your browser.

[http://localhost:8080/SpringActuator/health](http://localhost:8080/SpringActuator/health)

```json
{
"status":"UP",
"diskSpace":{"status":"UP","total":493767094272,"free":404976193536,"threshold":10485760},
"mongo":{"status":"UP","version":"3.0.2"}
}
```

[http://localhost:8080/SpringActuator/metrics](http://localhost:8080/SpringActuator/metrics)

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

	
	
**P.S.: If your Apache tomcat is running with IP then you'll required to "localhost" to "your IP address" and "8080" and configured in apache.**
	
###Configuration options:

In SpringActuator project there are some configuration available, which you can change based on your need. There is one property file "spring-admin.properties" available inside "/resource" folder. You can configure some properties as per your requirement. 

**For example:**

spring.application.name=SpringActuator

spring.boot.admin.url=[http://localhost:8081](http://localhost:8081)
	
	

