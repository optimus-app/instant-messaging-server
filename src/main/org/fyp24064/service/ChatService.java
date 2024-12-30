package org.fyp24064.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fyp24064.model.ChatMessage;
import org.fyp24064.model.ChatMessagePayload;
import org.fyp24064.model.ChatRoom;
import org.fyp24064.model.dto.CreateChatRoomDTO;
import org.fyp24064.repository.ChatRoomRepository;
import java.util.ArrayList;

@Service
public class ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public void createChatRoom(CreateChatRoomDTO chatRoomDTO) {
        ChatRoom chatRoom = ChatRoom.getBuilder()
                .setRoomTitle(chatRoomDTO.getRoomTitle())
                .setLastMessage("")
                .setMembers(chatRoomDTO.getMembers())
                .setMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);
    }

    public String forwardMessage(ChatMessagePayload messagePayload) {
        // TODO: Save message
        int chatRoomId = messagePayload.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatRoomId);
        ChatMessage chatMessage = ChatMessage.getBuilder()
                .setChatRoom(chatRoom)
                .setContent(messagePayload.getContent())
                .setSender(messagePayload.getSender())
                .build();
        chatRoom.addMessage(chatMessage);
        // TODO: Support group messagingTemplate send
        // Return type should be List<String>
        return String.format("/publish/chat/messages/{%s}", chatRoomId);

    }
}
