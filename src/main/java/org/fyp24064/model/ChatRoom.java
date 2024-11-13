package java.org.fyp24064.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String roomTitle;
    private List<String> members;
    private String lastMessage;
    private List<ChatMessage> messages;

    private ChatRoom(ChatRoomBuilder builder) {
        this.id = builder.id;
        this.roomTitle = builder.roomTitle;
        this.members = builder.members;
        this.lastMessage = builder.lastMessage;
        this.messages = builder.messages;
    }
    public static ChatRoomBuilder getBuilder() {
        return new ChatRoomBuilder();
    }
    public static class ChatRoomBuilder {
        private UUID id;
        private String roomTitle;
        private List<String> members;
        private String lastMessage;
        private List<ChatMessage> messages;

        public ChatRoomBuilder setId(UUID id) {
            this.id = id;
            return this;
        }

        public ChatRoomBuilder setRoomTitle(String roomTitle) {
            this.roomTitle = roomTitle;
            return this;
        }

        public ChatRoomBuilder setMembers(List<String> members) {
            this.members = members;
            return this;
        }

        public ChatRoomBuilder setLastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
            return this;
        }

        public ChatRoomBuilder setMessages(List<ChatMessage> messages) {
            this.messages = messages;
            return this;
        }

        public ChatRoom build() {
            return new ChatRoom(this);
        }

    }
}
