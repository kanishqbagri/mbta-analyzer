# MBTA Subway Lines Information API

This project provides a lightweight Java-based REST API to query MBTA subway (light and heavy rail) station data, including the subway lines that pass through each station, GPS coordinates, and adjacent stations (in progress). It consumes data from the [MBTA v3 API](https://api-v3.mbta.com/docs/swagger/index.html) and exposes relevant details via custom endpoints.

---

## 🚀 How to Install and Run Locally

## Prerequisites

Make sure you have the following installed:

- [Java 17+](https://adoptopenjdk.net/)
- [Apache Maven 3.8+](https://maven.apache.org/install.html)

### Check Installation

```bash
java -version
mvn -v

### Steps
1. **Clone the Repository**
   ```bash
   git clone https://github.com/kanishqbagri/mbta-analyzer.git
   cd mbta_api_2
2. Build the project
   mvn clean install

3. Run the Application
   mvn spring-boot:run


4. Test the API.
   Open a browser or a Rest client like Postman and execute the following commands:
   http://localhost:8080/api/stations/lines?routeID=70159


Assumptions and Design Decisions:
1. The Application would leverage a published Data that doesnt change frequently for the list of Requirements. Some part of the Application that relies on the List of Stations etc is cached and referenced to avoid mutiple API calls.
2. In order to keep this application light the data that is fetched, and processed is kept in the memory instead of adding support for a more permamnent persistent store
3. The application is designed to be a single threaded application with no concurrency
4. Error handling is limited to the HTTP  Error codes - 400, 404
5. The system is currently not designed for scalability; however has provisions to be redone
6. The limitations on certain APIs have been detailed in its corresponding swagger doc. Eg: Input is considered as the entire Station name. Partial values are currently not being honored.

Technical Design:
7. The Classes are broken into logical units to support re-usability and future enhancements
8. Java and Springboot chosen for quick setup, and REST friendly architecture. Also due to familiarity
9. HTTP Client: Java's native HTTP client and jackson parser are used to fetch and parse API responses

Challenges during Design and Implementation:
MBTA API filtering limits: The API doesn't provide the adjacency directly. I derived it from ordered stop sequences, and schedules. ADD SPECIFICS

Future Improvements:
TBD