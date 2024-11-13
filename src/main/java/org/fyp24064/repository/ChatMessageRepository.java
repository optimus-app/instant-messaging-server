package java.org.fyp24064.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.org.fyp24064.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
