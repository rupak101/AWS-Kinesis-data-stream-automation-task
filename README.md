# LiveIntent's QA Coding Challenge

The goal of this coding challenge is to have you produce automated tests that shows us in concrete 
terms how you think about QA software engineering in your professional life. We want you to use the 
languages, tools, and setup with which you feel most comfortable.

## Functional requirements

For this challenge, we ask you to write a test suite that covers the routing service for a fictional 
router system. This scenario is an extremely simplified version of some of the challenges that our 
engineering teams face at LiveIntent.

The service that you are going to test exposes a HTTP - GET - endpoint on `http://localhost:9000/route/:seed`.
It routes requests to two different [kinesis stream][kinesis] according the simple rules:
* If the *seed* received in the request is odd then it ends up to *li-stream-odd*
* If the *seed* received in the request is even then it ends up to *li-stream-even*

The routing service returns 200 when a valid number is received in the seed param and a message was sent
to kinesis stream. The response body is empty and a custom header `X-Transaction-Id` was added to Response Header.

Request / Response sample:

```shell script
curl --location -D - --request GET 'http://localhost:9000/route/1'

HTTP/1.1 200 OK
Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
X-Frame-Options: DENY
X-Transaction-Id: 325439c2-4b4e-45f1-98ee-75bc9e14d877
X-XSS-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Permitted-Cross-Domain-Policies: master-only
Date: Mon, 18 May 2020 11:50:23 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 0
```

In that case, with seed 1 (odd number), a message is sent to *li-odd-stream* stream.

```json
{"uuid": "325439c2-4b4e-45f1-98ee-75bc9e14d877", "seed": 1}
```

If seed is even, a message with same format is sent to *li-even-stream* stream.

We would like to check if messages are routing to respective streams correctly. If the seed is not a number
the services returns a bad request.

## Running the Route Service Locally

You will need Kinesis running on port 4568 and the routing service running on port 9000. We are providing a
docker compose file that manages the start / link of these two containers. You should run only:

```shell script
docker-compose up -d
```

To shutdown, run:

```shell script
docker-compose down
```

[kinesis]: https://aws.amazon.com/kinesis/data-streams/

# LiveIntent's QA Coding Challenge - Solution

<br/>This automated test suite covers all the test cases mentioned in code challenge file.

# Tools, Framework,Programming Language used: 
   * Intellij IDE, maven, RestAssured, TestNG, Java 8+,AWS kinesis client
   
# Libraries Used
* RestAssured:
    * To send the request to server and validate the response.
* Kinesis client:
    * To get the message from AWS kinesis and validate the response.    
* TestNG:
    * To perform parallel execution of test.

# Development environment : 
   * All development and execution done on Mac OS.It should work on other OS(e.g windows) as well. 
 
# Features:
* Generation human readable allure report
    - HTML Reports are available in the "/target/allure-report" directory having details of each test case execution.
* Configurator(via testng.xml file):
  * run tests in parallel mode;
    - Test cases executed in parallel with multiple threads.
* Allure report: 
  *Integrate to defect tracking system by using @link
  *Test order by severity by using @Severity annotation.
  *Tests groups with @Epic, @Feature, and @Stories annotations.

# Steps to execute the Tests:
* Method to run in Terminal:
    * Go to project folder in the terminal or command line
    * Running the Route Service Locally
    ```shell script
    docker-compose up -d
    ```
    * Execute via terminal or command line by entering below command.
    ```
    mvn clean test allure:report -D kinesisEndpoint=http://localhost:4568
    ``` 
    Run the command to generate allure report and open it in a browser: 
    ```bash
    mvn allure:serve
    ```