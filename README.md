##Requirements:
*  JDK 11 (set JAVA_HOME environment variable *)
*  Maven

## Run Instruction:
1. open a terminal in the request-validator directory.

1. Build a jar file with Maven:
   * $ mvn clean package
1. Run the jar file:
   * $ java -jar target/request-validator-0.0.1-SNAPSHOT.jar
1. Open following URL in your browser to see API documentation:
   * http://localhost:8080/swagger-ui.html

## Project Information
This project assumes you need stats of all valid and invalid requests until current moment at any time.
So there is an update operation per each validation request on hourly_stats table.
If in each point of time you just need the stats of previous hours or previous days(not current hour), we can eliminate
the update operation and insert status(valid or invalid) of each request in another table 
(request-status{customerId,date,time,status}) and then there is a job which compute the hourly stats and insert them in
the hourly_stats table, and remove processed records from request-status table. in this way we can achieve a better
performance, as insert operations are faster than updates and request_validation and stats_computation can be done 
asynchronously.
we can even make it faster and publish each request-status in a message broker(kafka) and then a
subscriber insert them to request-status table.

## Task Definition Improvements
It is better to clarify followings in the task definition:
* Requests are generate by your collector so if there is a malformed json should I count it as invalid for the customer?
if so, it's better to send the customerID as a path variable or query parameter.
* Will your collector send me the User-Agent or should I compute it? 
* Should I count requests receiving from an IP or User-Agent which is in black-list, or prevent attackers from putting load on our database?
* Should I prevent a black-listed IP to access request-validator endpoint or also daily-stats endpoint?

### Libraries
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/)