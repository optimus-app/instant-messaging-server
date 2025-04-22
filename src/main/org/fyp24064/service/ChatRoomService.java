package org.fyp24064.service;

import org.fyp24064.model.ChatRoom;
import org.fyp24064.model.dto.CreateChatRoomDTO;
import org.fyp24064.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;

/**
 * Service for handling chat room operations
 * Provides methods for creating, updating, and managing chat rooms
 */
@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // Counter for generating room IDs
    private static final AtomicInteger roomIdCounter = new AtomicInteger(1);

    /**
     * Initialize the room ID counter based on existing rooms in the database
     */
    @PostConstruct
    public void initRoomIdCounter() {
        try {
            // Get all existing rooms from the repository
            List<ChatRoom> existingRooms = chatRoomRepository.findAll();
            
            if (!existingRooms.isEmpty()) {
                // Find the maximum room ID value
                int maxRoomId = existingRooms.stream()
                    .map(room -> {
                        try {
                            return room.getRoomId();
                        } catch (NumberFormatException e) {
                            return 0; // Skip non-numeric IDs
                        }
                    })
                    .max(Integer::compare)
                    .orElse(0);
                
                // Set the counter to max + 1
                roomIdCounter.set(maxRoomId + 1);
                System.out.println("Initialized room ID counter to: " + roomIdCounter.get());
            }
        } catch (Exception e) {
            System.err.println("Error initializing room ID counter: " + e.getMessage());
            // Keep the default value of 1 if initialization fails
        }
    }
    
    /**
     * Create a new chat room
     * @param roomTitle Title of the chat room
     * @param members List of initial members
     * @return The created chat room
     */
    public ChatRoom createChatRoom(String roomTitle, List<String> members) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(roomIdCounter.getAndIncrement());
        chatRoom.setRoomTitle(roomTitle);
        chatRoom.setMembers(new ArrayList<>(members));

        // Save to repository
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        
        // Notify all members about the new chat room
        for (String member : members) {
            String path = String.format("/subscribe/chat/room/update/%s", member);
            messagingTemplate.convertAndSend(path, savedRoom);
        }
        
        return savedRoom;
    }
    
    /**
     * Create a chat room from DTO
     * @param chatRoomDTO DTO containing room details
     * @return The created chat room
     */
    public ChatRoom createChatRoom(CreateChatRoomDTO chatRoomDTO) {
        return createChatRoom(
            chatRoomDTO.getRoomTitle(),
            chatRoomDTO.getMembers()
        );
    }
    
    /**
     * Get a chat room by ID
     * @param roomId ID of the chat room
     * @return The requested chat room or null if not found
     */
    public ChatRoom getChatRoomById(int roomId) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(String.valueOf(roomId));
        return chatRoomOpt.orElse(null);
    }
    
    /**
     * Get all chat rooms for a user
     * @param userId User ID to search for
     * @return List of chat rooms the user is a member of
     */
    public List<ChatRoom> getChatRoomsForUser(String userId) {
        return chatRoomRepository.findByMembersContaining(userId);
    }
    
    /**
     * Add members to an existing chat room
     * @param roomId The chat room ID
     * @param newMembers List of new members to add
     * @return Updated ChatRoom
     */
    public ChatRoom addMembersToChatRoom(int roomId, List<String> newMembers) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(String.valueOf(roomId));
        if (!chatRoomOpt.isPresent()) {
            throw new IllegalArgumentException("Chat room not found");
        }
        
        ChatRoom chatRoom = chatRoomOpt.get();
        List<String> currentMembers = chatRoom.getMembers();
        List<String> addedMembers = new ArrayList<>();
        
        for (String member : newMembers) {
            if (!currentMembers.contains(member)) {
                currentMembers.add(member);
                addedMembers.add(member);
            }
        }
        
        if (addedMembers.isEmpty()) {
            return chatRoom; // No changes needed
        }
        
        chatRoom.setMembers(currentMembers);
        ChatRoom updatedRoom = chatRoomRepository.save(chatRoom);
        
        // Notify all members about the update
        for (String member : currentMembers) {
            String path = String.format("/subscribe/chat/room/update/%s", member);
            messagingTemplate.convertAndSend(path, updatedRoom);
        }
        
        // Notify new members specifically
        for (String newMember : addedMembers) {
            String path = String.format("/subscribe/chat/room/added/%s", newMember);
            messagingTemplate.convertAndSend(path, updatedRoom);
        }
        
        return updatedRoom;
    }
    
    /**
     * Remove a member from a chat room
     * @param roomId The chat room ID
     * @param username The username to remove
     * @return Updated ChatRoom or null if room is now empty
     */
    public ChatRoom removeMemberFromChatRoom(int roomId, String username) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(String.valueOf(roomId));
        if (!chatRoomOpt.isPresent()) {
            throw new IllegalArgumentException("Chat room not found");
        }
        
        ChatRoom chatRoom = chatRoomOpt.get();
        List<String> members = chatRoom.getMembers();
        
        if (!members.contains(username)) {
            throw new IllegalArgumentException("User is not a member of this chat room");
        }
        
        // Keep a copy of the original members for notifications
        List<String> originalMembers = new ArrayList<>(members);
        
        // Remove the user
        members.remove(username);
        chatRoom.setMembers(members);
        
        // If this was the last member, delete the room
        if (members.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
            
            // Notify the removed user that the room is gone
            String path = String.format("/subscribe/chat/room/deleted/%s", username);
            messagingTemplate.convertAndSend(path, roomId);
            
            return null;
        }
        
        // Otherwise save the updated room
        ChatRoom updatedRoom = chatRoomRepository.save(chatRoom);
        
        // Notify remaining members
        for (String member : members) {
            String path = String.format("/subscribe/chat/room/update/%s", member);
            messagingTemplate.convertAndSend(path, updatedRoom);
        }
        
        // Notify the removed user
        String removedPath = String.format("/subscribe/chat/room/removed/%s", username);
        messagingTemplate.convertAndSend(removedPath, roomId);
        
        return updatedRoom;
    }
    
    /**
     * Update chat room details
     * @param roomId The chat room ID
     * @param roomTitle New room title
     * @return Updated ChatRoom
     */
    public ChatRoom updateChatRoomTitle(int roomId, String roomTitle) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(String.valueOf(roomId));
        if (!chatRoomOpt.isPresent()) {
            throw new IllegalArgumentException("Chat room not found");
        }
        
        ChatRoom chatRoom = chatRoomOpt.get();
        chatRoom.setRoomTitle(roomTitle);
        
        ChatRoom updatedRoom = chatRoomRepository.save(chatRoom);
        
        // Notify all members about the update
        for (String member : updatedRoom.getMembers()) {
            String path = String.format("/subscribe/chat/room/update/%s", member);
            messagingTemplate.convertAndSend(path, updatedRoom);
        }
        
        return updatedRoom;
    }
    
    /**
     * Delete a chat room
     * @param roomId The ID of the room to delete
     * @return true if deleted successfully
     */
    public boolean deleteChatRoom(int roomId) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(String.valueOf(roomId));
        if (!chatRoomOpt.isPresent()) {
            return false;
        }
        
        ChatRoom chatRoom = chatRoomOpt.get();
        List<String> members = new ArrayList<>(chatRoom.getMembers());
        
        chatRoomRepository.delete(chatRoom);
        
        // Notify all former members
        for (String member : members) {
            String path = String.format("/subscribe/chat/room/deleted/%s", member);
            messagingTemplate.convertAndSend(path, roomId);
        }
        
        return true;
    }
}