package us.puter.park.common.config;

import org.springframework.stereotype.Component;

@Component
public class CommonPatterns {

    public static final String YN = "^[YN]$";
    public static final String UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String NOT_HANGEUL = "^[^ㄱ-ㅎㅏ-ㅣ가-힣]+$";
}
