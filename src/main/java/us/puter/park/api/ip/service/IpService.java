package us.puter.park.api.ip.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import us.puter.park.api.ip.dto.IpResDto;
import us.puter.park.util.IpUtils;

@Service
public class IpService {

    public IpResDto getIp(HttpServletRequest request) {
        return IpResDto.builder()
                .ip(IpUtils.getRemoteIP(request))
                .build();
    }
}
