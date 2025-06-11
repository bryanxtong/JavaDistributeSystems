This includes a dubbo 3 triple example without idl that integrates the sentinel for flow control and degrade control

1. get the nacos docker-compose standalone from https://github.com/nacos-group/nacos-docker and run it
```
docker compose -f example/standalone-mysql.yaml up
```
2. run the sentinel dashboard on port 8080 and set flow rules and circuit breaker rules for dubbo client and server side on http://localhost:8080/
```
java -jar sentinel-dashboard-1.8.8.jar
```