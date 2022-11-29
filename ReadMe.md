# Microservice REST API #

## Client service ##
Consumes .csv file and stores data in Database.
Each line of .CSV file has to have 4 columns:
* PRIMARY_KEY [literal and numeric symbols];
* NAME [literal and numeric symbols, space];
* DESCRIPTION [literal and numeric symbols, space];
* UPDATED_TIMESTAMP [HH:mm];

### Endpoints: ###

- GET: (`localhost:8080/api/v1/client/{id}`) - To get information about Record by its PRIMARY_KEY. Could contain only literal
  characters and digits.
- POST: (`localhost:8080/api/v1/client/upload_csv`) - To upload .csv file.

## Owner service ##
Allows deleting specific Record by its PRIMARY_KEY.

### Endpoints: ###

- DELETE: (`localhost:8077/api/v1/owner`) - To delete Record by its PRIMARY_KEY.

## DOCKER ##
<b>In the root of the project docker-compose.yml file placed which allows running of the both services using Docker.
Owner service exposed on port 8077
Client service exposed on port 8080
Postgres service exposed on port 5432
PgAdmin service exposed on port 50500
<b>
