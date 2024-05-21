package us.puter.park.api.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import us.puter.park.common.config.CommonPatterns;

public record MenuCreateReqDto(
        @NotBlank
        String name,
        @NotBlank
        String mode,
        @NotBlank
        String uri,
        @NotBlank
        String icon,
        @NotBlank
        @Pattern(regexp = CommonPatterns.YN)
        String useYn
) {
}
