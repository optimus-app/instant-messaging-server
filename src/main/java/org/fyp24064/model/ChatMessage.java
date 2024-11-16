package java.org.fyp24064.model;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name="room_id", nullable = false)
    private ChatRoom chatRoom;

    private ChatMessage(ChatMessageBuilder builder) {
        this.id = builder.id;
        this.content = builder.content;
        this.sender = builder.sender;
        this.chatRoom = builder.chatRoom;
    }

    public static ChatMessageBuilder getBuilder() {
        return new ChatMessageBuilder();
    }
    public static class ChatMessageBuilder {
        private UUID id;
        private String content;
        private String sender;
        private ChatRoom chatRoom;

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

        public ChatMessage build() {
            return new ChatMessage(this);
        }

    }
}
