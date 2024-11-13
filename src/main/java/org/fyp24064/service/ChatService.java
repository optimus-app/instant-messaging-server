package java.org.fyp24064.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
}
