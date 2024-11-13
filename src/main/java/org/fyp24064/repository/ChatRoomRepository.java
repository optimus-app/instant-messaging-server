package java.org.fyp24064.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.org.fyp24064.model.ChatRoom;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    List<ChatRoom> findAllById(String userId);
}
