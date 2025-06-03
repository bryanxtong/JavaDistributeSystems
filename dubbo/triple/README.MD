1. get the nacos docker-compose standalone from https://github.com/nacos-group/nacos-docker and run it
```
docker compose -f example/standalone-mysql.yaml up

```

2. run the following command to generate proto java files for both provider and consumer folders

```
mvn clean compile
```
