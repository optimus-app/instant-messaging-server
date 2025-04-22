package org.fyp24064.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.fyp24064.model.ChatRoom;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    ChatRoom findByRoomId(int roomId);
    @Query("SELECT c FROM ChatRoom c JOIN c.members m WHERE m = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") String userId);
    List<ChatRoom> findByMembersContaining(String userId);
}