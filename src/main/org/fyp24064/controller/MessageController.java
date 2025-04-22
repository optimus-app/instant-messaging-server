package org.fyp24064.controller;

import org.fyp24064.model.ChatMessagePayload;
import org.fyp24064.model.ChatRoom;
import org.fyp24064.model.dto.CreateChatRoomDTO;
import org.fyp24064.repository.ChatRoomRepository;
import org.fyp24064.service.ChatService;
import org.fyp24064.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class MessageController {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRoomService chatRoomService;

    /**
     *
     * @param message
     * @return
     * This is to test if the controller is working.
     * Type this command in your terminal:
     * curl -X POST http://localhost:8080/chat/ -H "Content-Type: application/json" -d '{"message": "Hello!"}'
     */
    @PostMapping(path = "/test")
    public String handlePostRequest(@RequestBody String message) {
        return "Message Received " + message;
    }

    /**
     * This function creates chat room in the repository
     * - Creates chat room locally
     * - Saves the new chat room into the repository
     * Note that the underlying business logic is written in createChatRoom
     * @param ChatRoomDTO
     * @return
     */
    @PostMapping(path = "/createRoom")
    public ResponseEntity<String> createChatRoom(@RequestBody CreateChatRoomDTO chatRoomDTO) {
        System.out.println(chatRoomDTO.getMembers());
        chatRoomService.createChatRoom(chatRoomDTO);
        List<String> roomMembers = chatRoomDTO.getMembers();
        for (String member : roomMembers) {
            String path = String.format("/subscribe/chat/creation/%s", member);
            messagingTemplate.convertAndSend(path, chatRoomDTO);
        }
        return ResponseEntity.ok("ChatRoom created!");
    }

    /**
     * This function does the following:
     * - Save the message in the database
     * - Broadcast the message to the subscribers of the channel endpoint
     * - User should subscribe to "/{roomId}/new_message" endpoint
     * @param messagePayload
     * @return
     */

    @PostMapping(value = "/message")
    public ResponseEntity<String> sendMessage(@RequestBody ChatMessagePayload messagePayload) {
        List<String> payload = chatService.forwardMessage(messagePayload);
        for (String p : payload) {
            System.out.println(p);
            messagingTemplate.convertAndSend(p, messagePayload);
        }
        return new ResponseEntity<String>("{\"message\": \"message sent\"}",HttpStatus.OK);
    }


    @GetMapping(path = "/chatRoom/{userId}")
    public List<ChatRoom> getChatRoomForUser(@PathVariable("userId") String userId) {
        // TODO: Create DTO on only transferring the roomId, roomTitle, lastMessage
        System.out.println(userId);
        return chatRoomRepository.findAllByUserId(userId);
    }

    // Done
    @GetMapping(path = "/messages/{roomId}")
    public ChatRoom getChatMessages(@PathVariable("roomId") int roomId) {
        System.out.println(roomId);
        return chatRoomRepository.findByRoomId(roomId);
    }

}
