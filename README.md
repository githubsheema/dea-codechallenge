# ipv4manager
## IPv4 Manager

Welcome to IPv4 Manager! It is a simple REST API that allows you to manage pool of IPv4 IP addresses using [CIDR format](https://whatismyipaddress.com/cidr). 

## How to Download

Clone this github to get the [source code for the project](https://github.com/githubsheema/ipv4manager.git).

## How to Run
### Eclipse way
Import project into your eclipse as maven project and Run as SpringBoot Application.

### Docker way
Pull docker image from docker.io using the following command
```bash
docker run -p 8080:8080 -it dockersheema/ipv4manager
```
To view sql activities
```bash
docker exec -it <<containerid>> /bin/sh -c "tail -f /logs/sql.log"
docker exec -it <<containerid>> /bin/sh -c "tail -f /logs/admin.log"
```

## How to test
Use Postman or any Rest Client and send PUT/GET requests to endpoints documented below.

Also Run as JUnit Test from Eclipse or ```mvn clean test```

### Application Endpoints

| Description   | EndPoint           | Method  |  Payload | 
| ------------- |:-------------:| :-----:|:-----:|
| Create IP Block      | <http://localhost:8080/createBlock> | PUT | text (e.g: 1.10.2.1/29) |
| GetAll IPs      | <http://localhost:8080/getAllIPs> | GET | N/A |
| Acquire an IP | <http://localhost:8080/acquireIP> | PUT | text (e.g: 1.10.2.2) |
| Release an IP | <http://localhost:8080/releaseIP> | PUT | text (e.g: 1.10.2.3) |

### Misc Endpoints
| Description   | EndPoint           | Purpose  |  
| ------------- |:-------------:| :-----:|
| Swagge-api      | <http://localhost:8080/swagger-ui.html#/ipv4_Manager> | RestAPI definitions |
| Actuator      | <http://localhost:8080/actuator>                        | Status of Services | 
| Hal-Browser      | <http://localhost:8080/>                             | Plain crud operations | 
| H2-Console | <http://localhost:8080/h2-console>                         | To view the in-memory DB. use *jdbc:h2:mem:testdb* as connection url |

### The REST API supports four endpoints…
  * Create IP addresses - take in a [CIDR block](https://whatismyipaddress.com/cidr) and add all IP addresses within that block to the database with status available”
  * List IP addresses - return all IP addresses in the system with their current status  
  * Acquire an IP - set the status of a certain IP to “acquired”
  * Release an IP - set the status of a certain IP to “available”
  * Supprots IPv4 format and Validates input for most posible CIDR format and IPv4 address formats.
---               
### Software Architecture
  * Uses Java 1.8x and SpringBoot 2.x framework
  * Consumes plain text payload as input
  * Returns responses in JSON format
  * Uses in memory database H2
  * Available to run as docker image
  * Provides consistent responses
  * Swagger api is available for REST API contract
  * Actuator is available for Service monitoring
---                
### Limitations
  * Supports IPv4 Only  
  * PUT methods supported for create/acquire/release and GET method for GetAllIPs
  * Both network / broadcast addresses inclusive. e.g. 1.1.1.1/32 and 1.1.1.1/31 are allowed
  * It uses in memory H2 DB. Attempting to provision /16, /8 addresses would hit performance. However, tested with /16 successfully.
  * Limited unit tests done. *Controller and Service classes are unit tested*
  * Exception handling is mapped to appropriate except 500 Internal errors. (Need to look into it)

---


### Example Successful Responses

*Create IP Block Response* **200 OK**

```json
{
    "block": "1.12.1.9/32",
    "createDate": "2019-01-06T17:55:03.617+0000",
    "createdBy": "tester",
    "modifiedDate": "2019-01-06T17:55:03.617+0000",
    "_links": {
        "to-get-ips-click-here": {
            "href": "http://127.0.0.1:8080/getAllIPs"
        }
    }
}
```
*Get All IPs* **200 OK**
```json
[
    {
        "ipAddress": "1.12.2.9",
        "netMask": "255.255.255.255",
        "status": "AVAILABLE",
        "createdBy": "tester",
        "modifiedDate": "2019-01-06T17:28:47.254+0000",
        "createDate": "2019-01-06T17:28:47.254+0000"
    }
]
```

*Acquire an IP* **200 OK**
```json
[
    {
        "ipAddress": "1.12.1.9",
        "netMask": "255.255.255.255",
        "status": "ACQUIRED",
        "createdBy": "tester",
        "modifiedDate": "2019-01-06T18:30:25.248+0000",
        "createDate": "2019-01-06T17:55:03.659+0000"
    }
]
```

*Release an IP* **200 OK**
```json
[
    {
        "ipAddress": "1.12.1.9",
        "netMask": "255.255.255.255",
        "status": "AVAILABLE",
        "createdBy": "tester",
        "modifiedDate": "2019-01-06T18:31:28.575+0000",
        "createDate": "2019-01-06T17:55:03.659+0000"
    }
]
```

### Example Failed Responses

*Block alreadey allocated* **409 Conflict**
```json
{
    "timestamp": "2019-01-06T18:33:01.825+0000",
    "message": "[1.12.1.9/32] - exists alreaady!",
    "details": "uri=/createBlock"
}
```

*Conflicts with existing Block*  **409 Conflict**
```json
{
    "timestamp": "2019-01-06T18:36:07.101+0000",
    "message": "[1.12.1.9] - already exists - check [1.12.1.64/24]!",
    "details": "uri=/createBlock"
}
```

*Invaid CIDR format*ad
for input 1.12.1.255/a **400 Bad Request**
```json
{
    "timestamp": "2019-01-06T18:36:29.905+0000",
    "message": "createIPBlock.block: must meet CDIR format : https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing",
    "details": "uri=/createBlock"
}
```

for input 1.12.1.255/999  **400 Bad Request**
```json

{
    "timestamp": "2019-01-06T18:37:14.575+0000",
    "message": "Value [999] not in range [0,32]",
    "details": "uri=/createBlock"
}
```

when acquire Request sent on an existing acquired IP  **409 Conflict**
```json
{
    "timestamp": "2019-01-06T18:39:23.772+0000",
    "message": "[1.12.1.9] - has [ACQUIRED] status alreaady!",
    "details": "uri=/acquireIP"
}
```

when release Request sent on an existing available IP  **409 Conflict**
```json
{
    "timestamp": "2019-01-06T18:40:07.595+0000",
    "message": "[1.12.1.9] - has [AVAILABLE] status alreaady!",
    "details": "uri=/releaseIP"
}
```

