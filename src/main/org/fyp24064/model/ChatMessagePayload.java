package org.fyp24064.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessagePayload {
    private String content;
    private int id;
    private int roomId;
    private String sender;
}
