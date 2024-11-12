package java.org.fyp24064.controller;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.org.fyp24064.repository.ChatMessageRepository;

@RestController
@RequestMapping(path = "/chat")
public class SubscriberController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping(path = "/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable("roomId") Long roomId) {
        // TODO: Add logic on retrieving data in the chat room
        return new ResponseEntity<>("Returning...", HttpStatus.FOUND);
    }
}
