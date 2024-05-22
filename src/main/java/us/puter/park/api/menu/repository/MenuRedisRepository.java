package us.puter.park.api.menu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import us.puter.park.api.menu.dto.MenuDto;
import us.puter.park.util.SafeCaster;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class MenuRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String KEY = "menuList";

    /**
     * redis에 menuList 저장
     * @param menuList
     */
    public void cacheMenuList(List<MenuDto> menuList) {
        redisTemplate.opsForValue().set(KEY, menuList);
        redisTemplate.expire(KEY, 60, TimeUnit.MINUTES);
    }

    /**
     * redis에서 menuList 조회
     * @return
     */
    public List<MenuDto> findMenuList() {
        return SafeCaster.castToList(redisTemplate.opsForValue().get(KEY));
    }
}
