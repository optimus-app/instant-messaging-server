# Instant Messaging Server

## Description

This is a server dedicated to the terminal for instant messaging. It allows the clients to do the following:

- Send a message to a specific user / group
- Retrieve all the groups that the user is a part of 

## Features to be implemented

- (Half-way) Create a group with different users
- Query the messages of the recent 10 messages of a chat channel (i.e. user or group)
- (Client-side) Establish a connection with the server, and subscribe to a path to receive messages (i.e. `/chat/messages/<username>`)

## Dependencies
For python-client, please install:
```bash
$ pip install stomp.py websocket-client
```
## Project Structure

- `python-client/main.py`: Contains a mock client implementation of the server. It connects with the websocket server of this project and sends messages to the server to test the server functionalities.
- `controller/`: Contains the controllers for the server, handles all the web requests and responses. For example, if a GET request is made to `/chat/messages/<username>`, the controller will handle the request and return the messages of the user. 
- `service/`: Contains the services for the server, handles all the business logic. For example, if a user wants to send a message to another user, the controller will pick up the request, and call the service functions for the implementation of the logic.
- `repository/` : Contains the repository for the server, handles all the database operations. For example, if a user wants to send a message to another user, the service will call the repository functions to store the message in the database.
- `model/`: Contains the models for the server, handles all the data structures. For example, if a user wants to send a message to another user, the service will call the model functions to create a message object. In short, it basically stores all the schema of all data, including message payload, database schema, etc.
- `config/`: Contains the configuration for the server, handles all the server configurations for WebSockets, i.e. opening up the relevant 

## Logic and paths

If the clients want to connect with the server, the relevant endpoint would be (assume the server is hosted locally and port is 8080):

```
http://localhost:8080/connect/chat
```

If the clients want to subscribe messages from the server, the relevant endpoint would be:

```
http://localhost:8080/subscribe/chat/message/<username>
```

If the server publish messages through `SimpMessagingTemplate`, the relevant path would be:

```
/publish/chat/message/<username>
```
If the clients want to create a group, the relevant endpoint would be:

```
POST http://localhost:8080/chat
```

If the clients want to send a message to a person, include the payload in the following path:

```
POST http://localhost:8080/chat/message
```

If the clients want to retrieve all the groups that the user is a part of, the relevant endpoint would be:

```
GET http://localhost:8080/chat/chatRoom/<username>
```

If the clients want to retrieve the messages of the recent 10 messages of a chat channel (i.e. user or group), the relevant endpoint would be:

```
GET http://localhost:8080/chat/messages/<chatChannelId>
```