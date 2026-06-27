#  API GATEWAY

1. It is a spring boot project used as a gateway server. It is used to route the request to the different microservices.
2. It is a microservice that acts as an interface between a client and a service. User send request to gateway, gateway will route the request to other services and take response from the service. And gateway will send response back to the client.
3. Before sending request to services and after reciving response from the service, it can manipulate or intercept the request and response.

### Diff Purpose of API GATEWAY
##### 1. Security
* Authorization :- When user send the request for a service then before sending the request, We can authenticate the user that it has access to the service or not. 
* Rate Limiting:- A user send the request again and again very frequently for fun or hacking purpose then it increase the load on the service. Server was processing the request even client does not need and this make server slow or not able to process the request. It is also called DDoS attack. To prevent this We can set that how many request a user can send in a time interval and after that server will not process the request comming from that user for that time interval.
* There are different type of attack like CSRF, XXS, XSS, CSRF, etc. can be blocked by API Gateway. Here it is important to configure our services to accept requst only from gateway or other services. Other machine cannot send request to our service.

##### 2. Traffic Management
Gateway recive the requet from the client, process the url and redirect the request to appropriate service this is called traffic management.
* Static Routing :- In this type of traffic management, we can configure the url and redirect the request to the service.
```
if url is /abc/** then redirect to service abc
```

* Dynamic Routing :- In this type of traffic management, Gateway decide according to the logic that which URL should be redirected to which service.

##### 3. Monitoring and Logging
We can moniter that how much request are coming,  successful response, failed response, exception, time taken to process the request etc.  
We can log the request and response so that we can later analyze it on which url and for which user response faild. what is the cause of failure or many more.

##### 4. Others
* Caching
* Load balancing :- How much time wait for the response, if A service is down then what should we do etc.
* SSL
* Modification on the request and response

## ZUUL API GATEWAY

It is open source project developed by Netflix but now spring doesn't support it means it is depricated scence 2.5

1. Add depedency, Configure Url routing, 
Depedency
```
<!-- Dependency for zuul API Gateway -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zuul</artifactId>
    <version>1.4.6.RELEASE</version>
</dependency>
```
.yml configuration
```
zuul:
  routes:
    service1:
      path: /service1/**
      url: http://localhost:8081
  host:
    connect-timeout-millis: 2000
    socket-timeout-millis: 10000
```
2. Filter and Exception Handling
There are four types of filters postFilter, routeFilter, preFilter and errorFilter. All filter extends ZuulFilter.
* PreFilter :- It filter request before redirecting to srevice even before spring authentication.
* PostFilter :- It filter response after reciving response from service. It is used to manipulate response.

## Spring Cloud Gateway

It is part of spring cloud framework. It is used to route the request to the different microservices. It comes in two verients MVC and Reactive. Reactive is used for high performance and low latency. As we have familiar with MVC I am going to use this.

1. Add depedency, Configure Url routing, 
Depedency
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
.yml configuration
```
spring:
  cloud:
      gateway:
        mvc:
          routes:
            - id: DashboardLegacys
              uri: http://localhost:8081
              predicates:
                - Path=/service1/**
              filters:
                - StripPrefix=1
              
```
Now it will route the url starts with /service1/** to http://localhost:8081. StripPrefix is used to remove /service1 from the url.


2. Filter and Exception Handling
* It doesn't have specific filter like zuul but we can use multiple filters. I haven't gone through it yet.