package org.fyp24064.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fyp24064.model.ChatMessage;
import org.fyp24064.model.ChatMessagePayload;
import org.fyp24064.model.ChatRoom;
import org.fyp24064.model.dto.CreateChatRoomDTO;
import org.fyp24064.repository.ChatRoomRepository;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    private static final String URL_PATH_BASE = "/subscribe/chat/messages/%s";
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    public List<String> forwardMessage(ChatMessagePayload messagePayload) {
        int chatRoomId = messagePayload.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatRoomId);
        ChatMessage chatMessage = ChatMessage.getBuilder()
                .setChatRoom(chatRoom)
                .setContent(messagePayload.getContent())
                .setSender(messagePayload.getSender())
                .setTimestamp(messagePayload.getTimestamp())
                .build();
        chatRoom.addMessage(chatMessage);
        chatRoomRepository.save(chatRoom);

        Object[] chatRoomMembers = chatRoom.getMembers().toArray();
        List<String> allMembersPath = new ArrayList<>();
        for (Object user : chatRoomMembers) {
            System.out.println(user);
            allMembersPath.add(String.format(URL_PATH_BASE, user));
        }

        return allMembersPath;

    }
}
