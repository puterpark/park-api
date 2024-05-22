package us.puter.park.api.shortenurl.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlCreateReqDto(
        @NotBlank
        @URL
        String orgUrl
) {
}
