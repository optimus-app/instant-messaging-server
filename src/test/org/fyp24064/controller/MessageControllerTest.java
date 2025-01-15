package org.fyp24064.controller;

import static org.mockito.ArgumentMatchers.any;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fyp24064.model.ChatMessage;
import org.fyp24064.model.ChatRoom;
import org.fyp24064.model.dto.CreateChatRoomDTO;
import org.fyp24064.repository.ChatRoomRepository;
import org.fyp24064.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomRepository chatRoomRepository;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @MockBean
    private ChatService chatService;

    @Test
    void testHandlePostRequest() throws Exception {
        String message = "Hello!";

        mockMvc.perform(post("/chat/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(message))
                .andExpect(status().isOk())
                .andExpect(content().string("Message Received " + message));
    }

    @Test
    void createChatRoom_Success() throws Exception {
        CreateChatRoomDTO dto = new CreateChatRoomDTO();
        dto.setMembers(Arrays.asList("user1", "user2"));

        doNothing().when(chatService).createChatRoom(any(CreateChatRoomDTO.class));

        mockMvc.perform(post("/chat/createRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("ChatRoom created!"));
    }

    @Test
    void getChatRoomForUser_Success() throws Exception {
//        String userId = "user1";
//        List<ChatRoom> mockRooms = Arrays.asList(
//                new ChatRoom(1, "Room 1", "Last message 1"),
//                new ChatRoom(2, "Room 2", "Last message 2")
//        );
//
//        when(chatRoomRepository.findAllByUserId(userId)).thenReturn(mockRooms);
//
//        mockMvc.perform(get("/chat/chatRoom/{userId}", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].roomTitle").value("Room 1"))
//                .andExpect(jsonPath("$[1].roomTitle").value("Room 2"));
    }

    @Test
    void getChatMessages_Success() throws Exception {
        int roomId = 1;
        ChatRoom mockRoom = new ChatRoom();
        mockRoom.setRoomTitle("Test");
        mockRoom.setLastMessage("Last");
        mockRoom.setRoomId(1);

        // Using the builder pattern for ChatMessage
        List<ChatMessage> mockMessages = Arrays.asList(
                ChatMessage.getBuilder()
                        .setContent("Message 1")
                        .setSender("user1")
                        .setChatRoom(mockRoom)
                        .build(),
                ChatMessage.getBuilder()
                        .setContent("Message 2")
                        .setSender("user2")
                        .setChatRoom(mockRoom)
                        .build()
        );
        mockRoom.setMessages(mockMessages);

        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(mockRoom);

        mockMvc.perform(get("/chat/messages/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Message 1"))
                .andExpect(jsonPath("$[1].content").value("Message 2"));
    }

    private String asJsonString(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
