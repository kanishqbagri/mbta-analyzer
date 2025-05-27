Test Strategy Document

## Overview
MBTA Station Info project aims to act as an interface between the End user and the MBTA API Gateway. The objective is to fetch some of the information about each station and present it to the user when accessed via the API layer.
## Objectives
The objective of this Test strategy document is to ensure the MBTA Station Info Application functions as mentioned in the Scope. The document covers the overall Test strategy for testing the application.  
## Scope
The scope of the project includes:
1. Testing the application via the published API
2. Testing different conditions for the 3 data points for each station
3. Data Validation of the Response to certify the accuracy of the Data
4. Error Handling

## Out of Scope
1. User Interface Testing

## Testing Strategy
The Testing Strategy would involve testing the application Manually and Automation using appropriate tools. 
1. Unit Testing: The code level testing will be covered by Unit testing 
2. Manual Testing: Using a Rest client like Postman / Insomnia to certify the P1 tests that certify the Acceptance criteria for the project.
2. Automated Testing: Using a framework like Rest Assured to test the APIs for different Data points. The Data points will include different stations with 1,2,3, adjacent Stations etc.
3. The automated tests will be packaged in Test suites which will be configured to run against the Application at scheduled times to ensure Application reliability.
4. System Testing: The application will be tested in the QA/Pre prod environment leveraging valid user credentials. It will also be tested with other Application components to ensure
5. Test Case Management: All the Test cases will be tracked in JIRA

### Functional Positive 
Test scenarios:
1. API should return HTTP 200 when called from the Rest client
2. Ensure the Response covers all the Stations on each line
3. Ensure the structure of each object has all the 3 Data points:
   1. Name
   2. Latitude and Longitude
   3. The Lines that pass through that station
   4. The list of Adjacent stations 
4. Test the result for accuracy of Data
5. Verify the stations with 1,2,3,4 adjacent stations reflect correct data
   1. Adjacent: Alewife, 
   2. Adjacent: Central, 
   3. Adjacent: Lechmere, 
   4. Adjacent stations: Downtown Crossing

### Functional Negative
The scope for Negative testing is limited 'coz the Application currently just exposes 1 API that takes a GET call'
However, in the event the application takes the stopId as an additional parameter to return the info per station:
1. Ensure the Application handles scenarios where the stopId is Invalid i.e. alpha numeric
2. Ensure the Application handles the scenario where the stopId is a number that doesnt map to a station
3. Ensure the Application handles the scenario where a Stop thats out of commission due to repair responds appropriately
### Security Testing
1. Send a POST, DELETE, UPDATE call to the same API
2. In a scenario where the application takes a valid Auth token to respond, send invalid tokens
3. Test with authenticated but UN Authorized token
### Load/Performance Testing
1. Make multiple parallel calls to the API to benchmark the performance time

### Automation
Automated Testing: Using a framework like Rest Assured to test the APIs for different Data points. The Data points will include different stations with 1,2,3, adjacent Stations etc.

### Test Data Strategy
Live MBTA Data will be used for Testing

### Error Handling
1. Confirm 500 is not exposed to the client due to internal exceptions.
2. Simulate upstream MBTA API failures (timeouts, 5xx) and validate fallback or proper error message.
3. Ensure error messages follow a consistent schema (code, message, timestamp).

## Risks and Mitigation
In a scenario if the MBTA API changes, Or the Lines get updated due to new stations or Updates, the caching logic will need to be updated

## Observability
1. Ensure the application logs all requests with response times and status codes. Monitor via Log monitoring via  Kibana/ Elastic Search
2. Monitor the following metrics for the App when deployed in production (Prometheus + Grafana):
   1. Request Count
   2. Error Count
   3. CPU/Memory
4. Health Check
   5. External MBTA API connectivity
   
## Release Process
1. Code changes should undergo peer review and static analysis .
2. All tests (unit, functional, integration) must pass in CI/CD pipeline.
3. Smoke test suite should be executed successfully on Pre-prod before production deployment.
4. Version tagging and changelog updates for every release.
5. Rollback strategy documented and tested (manual or automated).

## Stake Holders
1. **Product Owner** 
2. **QA Team** 
3. **Developers** 
4. **DevOps** 
