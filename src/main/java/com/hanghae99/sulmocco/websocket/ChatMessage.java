package com.hanghae99.sulmocco.websocket;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChatMessage {

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK
    }

    private MessageType type; // 메시지 타입
    private String chatRoomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
}
