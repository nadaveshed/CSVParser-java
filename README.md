# CSV Parser Project
This project is a CSV parser built using Spring Boot, designed to read player data from a CSV file, fetch additional information from an external API, and store the data in a PostgreSQL database. It includes functionalities such as retrieving player information via HTTP GET requests and updating player data periodically using scheduled tasks.

## Technologies Used
- Java
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Spring Messaging (SimpMessagingTemplate)
- Spring Scheduling
- Apache HttpClient
### Setup Instructions
1. Clone the Repository:

```
git clone https://github.com/your_username/csv-parser.git
```
2. Set up PostgreSQL Database:

- Install PostgreSQL on your local machine or use a cloud-based PostgreSQL service.
- Create a database named `csvparser` or any desired name.
- Update the database configurations in application.properties file located in `src/main/resources`.
3. Place CSV File:

Put the CSV file containing player data in the `src/main/resources` directory. Ensure that the file is named `players.csv`.
4. Build and Run the Application:

```
cd csv-parser
./mvnw spring-boot:run
```
5. Access the Application:

Once the application is running, you can access it at ``` `http://localhost:8080` ```.
6. Endpoints:

- `/player`: HTTP GET endpoint to retrieve player information from the CSV file and API.
  
### Important Notes
PostgreSQL Configuration: Ensure that the PostgreSQL database is properly configured, and the credentials in `application.properties` match your PostgreSQL setup.

External API: The application fetches additional player information from an external API (`https://api.balldontlie.io/v1/players/`). Ensure that the API is accessible and responsive for the application to function properly.

Scheduled Task: The application includes a scheduled task (`updatePlayerData()`) that runs every 15 minutes to update player data from the API.

WebSocket Messaging: WebSocket messaging is used to notify clients about player updates. The messaging endpoint is `/topic/playerUpdate`.
