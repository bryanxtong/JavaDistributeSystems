This example contains gprc client/server integration with nacos using spring cloud nacos discovery and how gprc server validates oauth token with grpc-spring library https://github.com/grpc-ecosystem/grpc-spring

How to use

1. get the nacos docker-compose standalone from https://github.com/nacos-group/nacos-docker and build it and then access to it via http://localhost:8080/
```
docker compose -f example/standalone-mysql.yaml up
```
2. start the authorization server which is working on port 9000

3. start gprc server multiple instances

4. start the grpc client http://localhost:8082/api/unary/bryan with user (user1/password), as 8080 is using by nacos by default.It will load balance the requests between the servers using round robin.

5. The instance that registered has metadata is like below:
{
	"preserved.register.source": "SPRING_CLOUD",
	"IPv6": "[240e:b8f:5d3a:7200:ed50:68be:e47e:11a3]",
	"gRPC_port": "14610"
}