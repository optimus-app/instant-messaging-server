package org.fyp24064.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.fyp24064.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
