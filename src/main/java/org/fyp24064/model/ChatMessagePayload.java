package java.org.fyp24064.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessagePayload {
    private String content;
    private UUID id;
    private String roomId;
    private String sender;
}
