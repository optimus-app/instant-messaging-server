package java.org.fyp24064.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.org.fyp24064.model.ChatMessagePayload;
import java.org.fyp24064.model.ChatRoom;
import java.org.fyp24064.model.dto.CreateChatRoomDTO;
import java.org.fyp24064.repository.ChatRoomRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/chat")
public class PublisherController {
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @PostMapping(path = "/")
    public ChatRoom createChatRoom(@RequestBody CreateChatRoomDTO chatRoomDTO) {
        // TODO: Create entities for chat rooms
        ChatRoom chatRoom = ChatRoom.getBuilder()
                .setRoomTitle(chatRoomDTO.getRoomTitle())
                .setId(UUID.randomUUID())
                .setLastMessage("")
                .setMembers(chatRoomDTO.getMembers())
                .setMessages(new ArrayList<>())
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    @MessageMapping(value = "/chat/message")
    public ResponseEntity<?> sendMessage(ChatMessagePayload messagePayload) {
        String chatRoomId = messagePayload.getRoomId();
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if (chatRoom.isEmpty()) {
            return new ResponseEntity<>("Unable to send", HttpStatus.BAD_REQUEST);
        }
        // TODO: Add RedisPublisher to publish the message in this
        return new ResponseEntity<>("Sent", HttpStatus.ACCEPTED);
    }
}
