This repository provides spring grpc examples, and It has two versions, netty and servlet  

netty folder provides an example which is running on netty and using grpc native security and It needs spring-oauth2-authorization-server to get started in advance(token not cached and each request get one,need to enhance)
   
servlet folder provides an example which is running on servlet and using web security and It needs spring-oauth2-authorization-server to get started in advance(token not cached and each request get one,need to enhance)

spring-cloud-gateway-grpc folder provides an example which is runnning on netty and It integrates spring cloud gateway with spring grpc via jsonToGrpc filter(without security)

spring-cloud-gateway-oauth2-grpc folder provides an example which is runnning on netty and It integrates spring cloud gateway with oauth2 and spring grpc via jsonToGrpc filter(change source code to support)
