package us.puter.park.api.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.puter.park.api.member.domain.PrincipalDetails;
import us.puter.park.api.member.dto.MemberInfoDto;
import us.puter.park.api.member.repository.MemberRepository;
import us.puter.park.common.exception.BusinessException;
import us.puter.park.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberInfoDto member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_MEMBER);
        }

        return createUserDetails(member);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(String id) {
        MemberInfoDto member = memberRepository.findById(id);
        if (member == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_MEMBER);
        }

        return createUserDetails(member);
    }

    private UserDetails createUserDetails(MemberInfoDto member) {
        return new PrincipalDetails(
                String.valueOf(member.id()),
                member.username(),
                member.password(),
                member.role(),
                member.status()
        );
    }
}
