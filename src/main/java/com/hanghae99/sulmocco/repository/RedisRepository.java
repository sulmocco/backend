package com.hanghae99.sulmocco.repository;


import com.hanghae99.sulmocco.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RedisRepository {

    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    public static final String USER_USERNAME = "USER_USERNAME"; // 채팅룸에 입장한 유저 닉네임


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Room> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    // 모든 채팅방 조회
    public List<Room> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // 특정 채팅방 조회
    public Room findRoomById(String id) {
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    public void setUsername(String sessionId, String username) {
        hashOpsEnterInfo.put(USER_USERNAME, sessionId, username);
    }

    /* 세션에서 유저 닉네임 로드 */
    public String getUsername(String sessionId) {
        return hashOpsEnterInfo.get(USER_USERNAME, sessionId);
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) { hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }
    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public long getUserCount(String chatRoomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + chatRoomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String chatRoomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + chatRoomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String chatRoomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + chatRoomId)).filter(count -> count > 0).orElse(0L);
    }
}
