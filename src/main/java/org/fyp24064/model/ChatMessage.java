package java.org.fyp24064.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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
    private String roomId;

    private ChatMessage(ChatMessageBuilder builder) {
        this.content = builder.content;
        this.sender = builder.sender;
        this.id = builder.id;
        this.roomId = builder.roomId;
    }
    public static ChatMessageBuilder getBuilder() {
        return new ChatMessageBuilder();
    }
    public static class ChatMessageBuilder {
        private UUID id;
        private String content;
        private String sender;
        private String roomId;

        public ChatMessageBuilder setId(UUID id) {
            this.id = id;
            return this;
        }
        public ChatMessageBuilder setContent(String content) {
            this.content = content;
            return this;
        }
        public ChatMessageBuilder setSender(String sender) {
            this.sender = sender;
            return this;
        }
        public ChatMessageBuilder setRoomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }

    }
}
