package java.org.fyp24064.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.org.fyp24064.model.ChatMessage;
import java.org.fyp24064.model.ChatMessagePayload;
import java.org.fyp24064.model.ChatRoom;
import java.org.fyp24064.model.dto.CreateChatRoomDTO;
import java.org.fyp24064.repository.ChatRoomRepository;
import java.util.ArrayList;
import java.util.List;

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
        // TODO: Save Message
        int chatRoomId = messagePayload.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatRoomId);
        ChatMessage chatMessage = ChatMessage.getBuilder()
                .setChatRoom(chatRoom)
                .setContent(messagePayload.getContent())
                .setSender(messagePayload.getSender())
                .build();
        chatRoom.addMessage(chatMessage);
        String payload = String.format("/{%s}/new_message", chatRoomId);


        messagingTemplate.convertAndSend(payload, messagePayload);
    }

    @GetMapping(path = "/{userId}/chatRoom")
    public List<ChatRoom> getChatRoomForUser(@PathVariable("userId") String userId) {
        // TODO: Add findAllByUserId method, add implementation
        return chatRoomRepository.findAllById(userId);
    }

    @GetMapping(path = "/{roomId}/messages")
    public List<ChatMessage> getChatMessages(@PathVariable("roomId") int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        return chatRoom.getMessages();
    }

}
