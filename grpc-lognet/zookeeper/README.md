This folder contains an example that integrates with zookeeper and also validate oauth token in gprc server with grpc-spring library https://github.com/LogNet/grpc-spring-boot-starter.

In this example, gprc-client is treated as both an oauth2 client and a resource server(for validating token), and grpc-server a resource server to validate the token.
This library will by default use the appliation name as the service name. Each grpc server will write gprc.port to eureka metadata(zero is supported) 
and gppc client can then use this metadata to connect and load banlance requests between the servers.

How to use

1. get and run a zookeeper instance
```
docker run --name zookeeper -p 2181:2181 -e JVMFLAGS="-Dzookeeper.admin.enableServer=false" --restart always -d zookeeper:3.8.3
```
2. start the authorization server which is working on port 9000

3. start gprc server multiple instances

4. start the grpc client, It will load balance the requests between the servers using round robin. and It now has 5 seconds delay to refresh the server instances list.