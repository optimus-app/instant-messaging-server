package java.org.fyp24064.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="chat_room")
public class ChatRoom implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int roomId;

    private String roomTitle;

    @ElementCollection
    private List<String> members;

    private String lastMessage;

    @OneToMany(mappedBy="chatRoom", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<ChatMessage> messages;

    private ChatRoom(ChatRoomBuilder builder) {
        this.roomId = builder.roomId;
        this.roomTitle = builder.roomTitle;
        this.members = builder.members;
        this.lastMessage = builder.lastMessage;
        this.messages = builder.messages;
    }

    public void addMessage(ChatMessage chatMessage) {
        messages.add(chatMessage);
    }
    public static ChatRoomBuilder getBuilder() {
        return new ChatRoomBuilder();
    }
    public static class ChatRoomBuilder {
        private int roomId;
        private String roomTitle;
        private List<String> members;
        private String lastMessage;
        private List<ChatMessage> messages;

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
