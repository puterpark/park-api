package us.puter.park.api.shortenurl.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlUpdateReqDto(
        @NotBlank
        String shortenUri,
        @NotBlank
        @URL
        String orgUrl
) {
}
