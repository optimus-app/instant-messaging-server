package java.org.fyp24064.controller;

import org.springframework.web.bind.annotation.*;

import java.org.fyp24064.model.dto.CreateChatRoomDTO;

@RestController
@RequestMapping(path = "/chat")
public class PublisherController {
    @PostMapping(path = "/")
    public String createChatRoom(@RequestBody CreateChatRoomDTO chatRoomDTO) {
        // TODO: Add logic to create chat room
        // TODO: Create entities for chat rooms
        return "";
    }
}
