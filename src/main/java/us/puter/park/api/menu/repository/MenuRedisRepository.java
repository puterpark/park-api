package us.puter.park.api.menu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import us.puter.park.api.menu.dto.MenuDto;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class MenuRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String KEY = "menuList";

    public void cacheMenuList(List<MenuDto> menuList) {
        redisTemplate.opsForValue().set(KEY, menuList);
        redisTemplate.expire(KEY, 60, TimeUnit.MINUTES);
    }

    public List<MenuDto> findMenuList() {
        @SuppressWarnings("unchecked")
        List<MenuDto> list = (List<MenuDto>) redisTemplate.opsForValue().get(KEY);
        return list;
    }
}
