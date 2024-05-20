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
public enum MemberStatus {
    NORMAL("NORMAL", "정상")
    , DROP("DROP", "탈퇴")
    ;

    private final String value;
    private final String label;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MemberStatus forValue(String value) {
        for (MemberStatus memberStatus : MemberStatus.values()) {
            if (StringUtils.equalsIgnoreCase(memberStatus.getValue(), value)) {
                return memberStatus;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_ENUM_VALUE, "MemberStatus");
    }
}
