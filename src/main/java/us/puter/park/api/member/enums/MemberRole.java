package us.puter.park.api.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.thymeleaf.util.StringUtils;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum MemberRole {
    ADMIN("ADMIN", "관리자")
    , MEMBER("MEMBER", "회원")
    ;

    private final String value;
    private final String label;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MemberRole forValue(String value) {
        for (MemberRole memberRole : MemberRole.values()) {
            if (StringUtils.equalsIgnoreCase(memberRole.getValue(), value)) {
                return memberRole;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_ENUM_VALUE, "MemberRole");
    }
}
