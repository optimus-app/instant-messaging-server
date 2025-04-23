package org.fyp24064.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String content;
    private String sender;
    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name="room_id", nullable = false)
    @JsonBackReference // Without this, there will be circular reference
    private ChatRoom chatRoom;

    private ChatMessage(ChatMessageBuilder builder) {
        this.id = builder.id;
        this.content = builder.content;
        this.sender = builder.sender;
        this.chatRoom = builder.chatRoom;
        this.timestamp = builder.timestamp;
    }

    public static ChatMessageBuilder getBuilder() {
        return new ChatMessageBuilder();
    }
    
    public static class ChatMessageBuilder {
        private UUID id;
        private String content;
        private String sender;
        private ChatRoom chatRoom;
        private Instant timestamp;

        public ChatMessageBuilder setContent(String content) {
            this.content = content;
            return this;
        }
        
        public ChatMessageBuilder setSender(String sender) {
            this.sender = sender;
            return this;
        }
        
        public ChatMessageBuilder setChatRoom(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
            return this;
        }
        
        // Add setter for timestamp
        public ChatMessageBuilder setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ChatMessage build() {
            // If no timestamp provided, set to current time
            if (this.timestamp == null) {
                this.timestamp = Instant.now();
            }
            return new ChatMessage(this);
        }
    }
}
