package org.fyp24064.controller;

import org.fyp24064.model.ChatRoom;
import org.fyp24064.model.dto.ChatRoomMemberDTO;
import org.fyp24064.model.dto.CreateChatRoomDTO;
import org.fyp24064.model.dto.UpdateChatRoomDTO;
import org.fyp24064.repository.ChatRoomRepository;
import org.fyp24064.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Controller responsible for managing chat rooms and their membership
 * Handles creating, updating, deleting rooms and adding/removing users
 */
@RestController
@RequestMapping(path = "/chat/room")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRoomService chatRoomService;

    /**
     * This function creates chat room in the repository
     * - Creates chat room locally
     * - Saves the new chat room into the repository
     * Note that the underlying business logic is written in createChatRoom
     * @param ChatRoomDTO
     * @return
     */
    @PostMapping(path = "/createRoom")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomDTO chatRoomDTO) {
        try {
            System.out.println("Creating chat room with members: " + chatRoomDTO.getMembers());
            
            // Create the chat room
            ChatRoom createdRoom = chatRoomService.createChatRoom(chatRoomDTO);
            
            // Notify members about room creation
            List<String> roomMembers = chatRoomDTO.getMembers();
            for (String member : roomMembers) {
                String path = String.format("/subscribe/chat/creation/%s", member);
                messagingTemplate.convertAndSend(path, chatRoomDTO);
            }
            
            // Return JSON response
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "ChatRoom created successfully",
                "roomId", createdRoom.getRoomId(),
                "roomTitle", createdRoom.getRoomTitle()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error creating chat room",
                    "message", e.getMessage()
                ));
        }
    }
    
    /**
     * Get all chat rooms for a user
     * @param userId ID of the user
     * @return List of chat rooms the user is a member of
     */
    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<?> getUserChatRooms(@PathVariable("userId") String userId) {
        try {
            List<ChatRoom> userRooms = chatRoomRepository.findByMembersContaining(userId);
            return new ResponseEntity<>(userRooms, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching user chat rooms: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update chat room details
     * @param roomId ID of the chat room
     * @param updateDTO New details for the chat room
     * @return The updated chat room
     */
    @PutMapping(path = "/{roomId}")
    public ResponseEntity<?> updateChatRoom(
            @PathVariable("roomId") int roomId,
            @RequestBody UpdateChatRoomDTO updateDTO) {
        
        try {
            Optional<ChatRoom> roomOptional = chatRoomRepository.findById(String.valueOf(roomId));
            if (!roomOptional.isPresent()) {
                return new ResponseEntity<>("Chat room not found", HttpStatus.NOT_FOUND);
            }
            
            ChatRoom chatRoom = roomOptional.get();
            
            // Update room title if provided
            if (updateDTO.getRoomTitle() != null && !updateDTO.getRoomTitle().isEmpty()) {
                chatRoom.setRoomTitle(updateDTO.getRoomTitle());
            }
            
            // Save the updated room
            chatRoom = chatRoomRepository.save(chatRoom);
            return new ResponseEntity<>(chatRoom, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating chat room: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add users to an existing chat room
     * @param roomId ID of the chat room
     * @param memberDTO Contains the list of users to add
     * @return Updated chat room information
     */
    @PostMapping(path = "/{roomId}/addUsers")
    public ResponseEntity<?> addUsersToChatRoom(
            @PathVariable("roomId") int roomId,
            @RequestBody ChatRoomMemberDTO memberDTO) {
        
        try {
            Optional<ChatRoom> roomOptional = chatRoomRepository.findById(String.valueOf(roomId));
            if (!roomOptional.isPresent()) {
                return new ResponseEntity<>("Chat room not found", HttpStatus.NOT_FOUND);
            }
            
            ChatRoom chatRoom = roomOptional.get();
            List<String> currentMembers = chatRoom.getMembers();
            List<String> usersToAdd = memberDTO.getUsersToAdd();
            
            // Check if there are any new members to add
            boolean membersChanged = false;
            for (String user : usersToAdd) {
                if (!currentMembers.contains(user)) {
                    currentMembers.add(user);
                    membersChanged = true;
                }
            }
            
            if (!membersChanged) {
                return new ResponseEntity<>("All users are already members", HttpStatus.OK);
            }
            
            // Update the chat room with new members
            chatRoom.setMembers(currentMembers);
            chatRoomRepository.save(chatRoom);
            return new ResponseEntity<>(chatRoom, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding users: " + e.getMessage(), 
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Remove a user from a chat room
     * @param roomId ID of the chat room
     * @param userId ID of the user to remove
     * @return Success/failure message
     */
    @PostMapping(path = "/{roomId}/removeUser/{userId}")
    public ResponseEntity<?> removeUserFromChatRoom(
            @PathVariable("roomId") int roomId,
            @PathVariable("userId") String userId) {
        
        try {
            Optional<ChatRoom> roomOptional = chatRoomRepository.findById(String.valueOf(roomId));
            if (!roomOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Chat room not found"));
            }
            
            ChatRoom chatRoom = roomOptional.get();
            List<String> members = chatRoom.getMembers();
            
            if (!members.contains(userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User is not a member of this chat room"));
            }
            
            // Remove the user
            members.remove(userId);
            chatRoom.setMembers(members);
            chatRoomRepository.save(chatRoom);
            
            // Notify the removed user
            String removedPath = String.format("/subscribe/chat/room/removed/%s", userId);
            messagingTemplate.convertAndSend(removedPath, roomId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User removed from chat room"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error removing user",
                    "message", e.getMessage()
                ));
        }
    }
    
    /**
     * Leave a chat room (self-removal)
     * @param roomId ID of the chat room
     * @param memberDTO Contains username of the user who wants to leave
     * @return Success/failure message as JSON
     */
    @PostMapping(path = "/{roomId}/leave")
    public ResponseEntity<?> leaveChatRoom(
            @PathVariable("roomId") int roomId,
            @RequestBody ChatRoomMemberDTO memberDTO) {
        
        try {
            String username = memberDTO.getUsername();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username cannot be empty"));
            }
            
            Optional<ChatRoom> roomOptional = chatRoomRepository.findById(String.valueOf(roomId));
            if (!roomOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Chat room not found"));
            }
            
            ChatRoom chatRoom = roomOptional.get();
            List<String> members = chatRoom.getMembers();
            
            if (!members.contains(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User is not a member of this chat room"));
            }
            
            // Remove the user
            members.remove(username);
            chatRoom.setMembers(members);
            chatRoomRepository.save(chatRoom);

            // Notify the user that they left the room
            String leftPath = String.format("/subscribe/chat/room/left/%s", username);
            messagingTemplate.convertAndSend(leftPath, roomId);
            
            // Notify remaining members about the update
            for (String member : members) {
                String path = String.format("/subscribe/chat/room/update/%s", member);
                messagingTemplate.convertAndSend(path, chatRoom);
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully left the chat room"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Error leaving chat room",
                    "message", e.getMessage()
                ));
        }
    }
    
    /**
     * Get all members of a chat room
     * @param roomId ID of the chat room
     * @return List of members
     */
    @GetMapping(path = "/{roomId}/members")
    public ResponseEntity<?> getChatRoomMembers(@PathVariable("roomId") int roomId) {
        try {
            Optional<ChatRoom> roomOptional = chatRoomRepository.findById(String.valueOf(roomId));
            if (!roomOptional.isPresent()) {
                return new ResponseEntity<>("Chat room not found", HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(roomOptional.get().getMembers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching members: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}