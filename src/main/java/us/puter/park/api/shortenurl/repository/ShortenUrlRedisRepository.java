package us.puter.park.api.shortenurl.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import us.puter.park.api.shortenurl.dto.ShortenUrlDto;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ShortenUrlRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String KEY_PREFIX = "shortenUrl:";

    /**
     * redis에 shortenUrl 저장
     * @param shortenUri
     * @param shortenUrlDto
     */
    public void save(String shortenUri, ShortenUrlDto shortenUrlDto) {
        String key = KEY_PREFIX + shortenUri;
        redisTemplate.opsForValue().set(key, shortenUrlDto);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * redis에서 shortenUrl 조회
     * @param shortenUri
     * @return
     */
    public Optional<ShortenUrlDto> findByShortenUri(String shortenUri) {
        String key = KEY_PREFIX + shortenUri;
        ShortenUrlDto shortenUrlDto = (ShortenUrlDto) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(shortenUrlDto);
    }

    /**
     * redis에서 shortenUrl 삭제
     * @param shortenUri
     */
    public void delete(String shortenUri) {
        String key = KEY_PREFIX + shortenUri;
        redisTemplate.delete(key);
    }
}