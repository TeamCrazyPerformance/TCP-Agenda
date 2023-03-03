package tcp.project.agenda.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.MemberRepository;
import tcp.project.agenda.auth.exception.InvalidPasswordException;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.auth.infrastructure.JwtTokenProvider;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByUnivId(request.getId())
                .orElseThrow(() -> new MemberNotFoundException(request.getId()));

        if (!member.validatePassword(request.getPassword())) {
            throw new InvalidPasswordException(request.getId(), request.getPassword());
        }

        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()));
        return new TokenResponse(token);
    }
}
