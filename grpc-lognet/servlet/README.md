This folder contains an example on how to validate oauth token in gprc server for grpc-spring library https://github.com/LogNet/grpc-spring-boot-starter.

In this example, gprc-client is treated as both an oauth2 client and a resource server(for validating token), and grpc-server a resource server to validate the token. The client supports service discovery with spring cloud consul discovery using nameresolver.
This library will by default add a "grpc-" as a prefix to the appliation name as the service name, eg "gprc-gprc-server-demo" where grpc-server-demo is the application name.

How to use

1. download the consul and start with command for local development
```
consul agent -dev
```

2. start the authorization server which is working on port 9000

3. start gprc server multiple instances

4. start the grpc client, It will load balance the requests between the servers using round robin. and It now has 5 seconds delay to refresh the server instances list.