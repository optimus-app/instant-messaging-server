package java.org.fyp24064.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.org.fyp24064.model.ChatMessagePayload;
import java.org.fyp24064.model.ChatRoom;
import java.org.fyp24064.model.dto.CreateChatRoomDTO;
import java.org.fyp24064.repository.ChatRoomRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/chat")
public class MessageController {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * This function creates chat room in the repository
     * - Creates chat room locally
     * - Saves the new chat room into the repository
     * @param ChatRoomDTO
     * @return
     */
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

    /**
     * This function does the following:
     * - Save the message in the database
     * - Broadcast the message to the subscribers of the channel endpoint
     * - User should subscribe to "/{roomId}/new_message" endpoint
     * @param messagePayload
     * @return
     */
    @MessageMapping(value = "/chat/message")
    public void sendMessage(ChatMessagePayload messagePayload) {
        String chatRoomId = messagePayload.getRoomId();
        String payload = String.format("/{%s}/new_message", chatRoomId);
        messagingTemplate.convertAndSend(payload, messagePayload);
    }

    @GetMapping(path = "/{userId}/chatRoom")
    public List<ChatRoom> getChatRoomForUser(@PathVariable("userId") String userId) {
        // TODO: Add findAllByUserId method, add implementation
        return chatRoomRepository.findAllById(userId);
    }

    @GetMapping(path = "/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable("roomId") Long roomId) {
        // TODO: Add logic on retrieving data in the chat room
        return new ResponseEntity<>("Returning...", HttpStatus.FOUND);
    }
}
