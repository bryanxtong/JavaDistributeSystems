Oauth2-gateway-sentinel includes spring cloud gateway integration with oauth2 and sentinel for flow-control and degrade control(circuit breaker)
You can create three users in Keycloak with different roles to test
```
user    role
bryan   USER 
jimmy   ADMIN 
kate    ADMIN,MANAGER
```
In this example, the spring cloud gateway is only as a oauth2 client and app-services(or other backends) are treated as resource servers.
But if you want to test with postman for the backend microservices via gateway url, you can enable manually the spring cloud gateway
as a resource server only for testing.
```
for i in {1..20}; 
do 
curl -v -H "Authorization:Bearer ${access_token}" http://${gateway-url}:8082/api/user/1; 
done
```

Note: remember to update the clientIp to your docker host ip for sentinel dashboard.
```
transport:
	dashboard: localhost:8858
	port: 8719
	clientIp: 192.168.71.3
```