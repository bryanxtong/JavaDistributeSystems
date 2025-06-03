This example contains gprc client/server integration with consul using spring cloud discovery consul and how gprc server validates oauth token with grpc-spring library https://github.com/grpc-ecosystem/grpc-spring

How to use

1. download the consul and start with command for local development
```
consul agent -dev
```
2. start the authorization server which is working on port 9000

3. start gprc server multiple instances

4. start the grpc client http://localhost:8080/api/unary/bryan with user (user1/password), It will load balance the requests between the servers using round robin.