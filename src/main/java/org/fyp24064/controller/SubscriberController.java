package java.org.fyp24064.controller;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.org.fyp24064.model.ChatRoom;
import java.org.fyp24064.repository.ChatMessageRepository;
import java.org.fyp24064.repository.ChatRoomRepository;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class SubscriberController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

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
