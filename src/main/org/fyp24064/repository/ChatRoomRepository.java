package org.fyp24064.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.fyp24064.model.ChatRoom;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    ChatRoom findByRoomId(int roomID);
    @Query(value = "SELECT c FROM ChatRoom c WHERE :user_id MEMBER OF c.members")
    List<ChatRoom> findAllByUserId(@Param("user_id") String userId);
}
