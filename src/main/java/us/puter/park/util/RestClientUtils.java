package us.puter.park.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class RestClientUtils {

    private final RestClient restClient;

    public RestClientUtils() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(20))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
        this.restClient = RestClient.create(restTemplate);
    }

    /**
     * URL - GET 요청
     * @param url
     * @param responseType
     * @return
     * @param <T>
     */
    public <T> T get(String url, Class<T> responseType) {
        T result = null;

        try {
            ResponseEntity<T> response = restClient.get()
                    .uri(url)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .retrieve()
                    .toEntity(responseType);

            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() != null) {
                    result = response.getBody();
                }
            }

        } catch (Exception e) {
            log.error("API Connection(GET) Fail: url[{}], {}", url, e.getMessage());
            throw new BusinessException(ErrorCode.API_CONNECTION_FAIL, url);
        }

        return result;
    }

    /**
     * URL - POST 요청
     * @param url
     * @param data
     * @param responseType
     * @return
     * @param <T>
     */
    public <T> T post(String url, Map<String, ?> data, Class<T> responseType) {
        T result = null;

        try {
            ResponseEntity<T> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .body(data)
                    .retrieve()
                    .toEntity(responseType);

            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() != null) {
                    result = response.getBody();
                }
            }

        } catch (Exception e) {
            log.error("API Connection(POST) Fail: url[{}], {}", url, e.getMessage());
            throw new BusinessException(ErrorCode.API_CONNECTION_FAIL, url);
        }

        return result;
    }

}
