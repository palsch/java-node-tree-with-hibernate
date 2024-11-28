# Java Node-Tree with Hibernate Application

## Overview
This is a Spring Boot application that provides an API for managing hierarchical data structures. The application allows creating, updating, and deleting nodes in a tree-like structure.

## Prerequisites
- Java 17

## Setup

1. Clone the repository:
    ```sh
    git@github.com:palsch/java-node-tree-with-hibernate.git
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## API Endpoints

### Get All Antrags
```http
GET /api/ale_antrag
```
Fetches all Antrags.

### Create New Antrag
```http
POST /api/ale_antrag
```
Creates a new Antrag.

### Get Specific Antrag
```http
GET /api/ale_antrag/{antrag_id}
```
Fetches a specific Antrag by ID.

### Delete Specific Antrag
```http
DELETE /api/ale_antrag/{antrag_id}
```
Deletes a specific Antrag by ID.

### Update IbanQuestion
```http
PATCH /api/ale_antrag/{antrag_id}/child-nodes
```
Updates an IbanQuestion.

### Add a Child Node
```http
POST /api/ale_antrag/{antrag_id}/{child_question_id}/child-nodes
```
Adds a child node to a specific Antrag.

### Remove a Child Node
```http
DELETE /api/ale_antrag/{antrag_id}/child-nodes/{child_node_id}
```
Removes a child node from a specific Antrag.


To enable db trace logs, run the application with the `dbtrace` profile:
```sh
mvn spring-boot:run -Dspring-boot.run.profiles=dbtrace
```
