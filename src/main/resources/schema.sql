-- Create chat_room table first
DROP TABLE IF EXISTS chat_room_members;
DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS chat_room;
DROP SEQUENCE IF EXISTS chat_room_seq;

-- Create a new sequence
--CREATE SEQUENCE chat_room_seq
--    START WITH 1
--    INCREMENT BY 50;

CREATE TABLE chat_room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_title VARCHAR(255) NOT NULL,
    last_message VARCHAR(255)
);

-- Create chat_room_members after chat_room
CREATE TABLE chat_room_members (
    room_id INT NOT NULL,
    member VARCHAR(255) NOT NULL,
    PRIMARY KEY (room_id, member),
    FOREIGN KEY (room_id) REFERENCES chat_room(room_id) ON DELETE CASCADE
);

-- Create chat_message last
CREATE TABLE chat_message (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    sender VARCHAR(255) NOT NULL,
    room_id INT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_room(room_id) ON DELETE CASCADE
);