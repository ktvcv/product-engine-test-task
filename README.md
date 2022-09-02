## Prerequisites to run application

#### Java 11

#### Gradle 7.5

To run test use this command: `./gradlew clean assemble test`
Also, you can run it from your IDE.

### About task

This is a Spring Boot Application. Spring Boot version 2.5.6. Built with gradle 7.5, Feign is used for HTTP request,
also for test used Junit 5 and Mockito. In general, the app has
one [endpoint](https://github.com/ktvcv/product-engine-test-task/blob/master/src/main/java/com/productengine/productenginetesttask/api/RouterMessageController.java)
, which gets all routers' messages, stores it and from time to time(60 seconds in test case, anyway, you can change it
in
a [properties file](https://github.com/ktvcv/product-engine-test-task/blob/master/src/main/resources/application.properties)
, so like other params)checks them for possible problem, and if a problem is found, invoke save method. For messages
analysis used two
classes([for lost problem](https://github.com/ktvcv/product-engine-test-task/blob/master/src/main/java/com/productengine/productenginetesttask/service/problemanalyzer/LostRouterAnalyzer.java)
and [flapped problem](https://github.com/ktvcv/product-engine-test-task/blob/master/src/main/java/com/productengine/productenginetesttask/service/problemanalyzer/FlapperRouterAnalyzer.java))
that describes logic of one problem. They are processing in parallel. As a sender message implemented a
[random message sender](https://github.com/ktvcv/product-engine-test-task/blob/master/src/main/java/com/productengine/productenginetesttask/service/sender/RandomRouterSenderMessage.java)
and a
[mock message sender](https://github.com/ktvcv/product-engine-test-task/blob/master/src/main/java/com/productengine/productenginetesttask/service/sender/MockRouterSenderMessage.java)
, that mocks data and uses for testing. Test checks two problem scenarios(that is not quite correct) together to check
how the app will work in 'real' situations when two scenarios are possible. For this task, the clearing of processed
data was not implemented.





