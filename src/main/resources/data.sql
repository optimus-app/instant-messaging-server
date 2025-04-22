INSERT INTO chat_room (room_id, room_title, last_message)
VALUES
(1, 'General Chat', 'Hey everyone!'),
(2, 'Project Discussion', 'Presentation due tomorrow'),
(3, 'Gaming Room', 'Who’s up for a game tonight?');

INSERT INTO chat_message (id, content, sender, room_id, timestamp)
VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Hey everyone!', 'user1', 1, '2025-04-22T10:15:30.00Z'),
('550e8400-e29b-41d4-a716-446655440002', 'Welcome to General Chat!', 'user2', 1, '2025-04-22T10:16:45.00Z'),
('550e8400-e29b-41d4-a716-446655440003', 'Don’t forget about the presentation tomorrow.', 'user3', 2, '2025-04-22T13:30:00.00Z'),
('550e8400-e29b-41d4-a716-446655440004', 'Who’s up for a game tonight?', 'user4', 3, '2025-04-22T17:05:22.00Z'),
('550e8400-e29b-41d4-a716-446655440005', 'Let’s play at 9 PM!', 'user5', 3, '2025-04-22T17:07:15.00Z');

INSERT INTO chat_room_members (room_id, member)
VALUES
(1, 'user1'),
(1, 'user2'),
(1, 'user3'),
(2, 'user3'),
(2, 'user4'),
(3, 'user4'),
(3, 'user5');

DROP SEQUENCE IF EXISTS chat_room_seq;

CREATE SEQUENCE chat_room_seq
    START WITH (SELECT MAX(room_id) + 1 FROM chat_room)
    INCREMENT BY 50;