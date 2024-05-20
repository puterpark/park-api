package us.puter.park.common.http;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommonHttpResultDto {

    private String message;
    private int status;
    private String code;

    public CommonHttpResultDto(String message, int status, String code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }
}
