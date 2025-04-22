package org.fyp24064.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMemberDTO {
    private int roomId;
    private String username; // For single-user operations like leaving a room
    private List<String> usersToAdd; // For adding multiple users
}