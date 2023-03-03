package tcp.project.agenda.auth.ui.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tcp.project.agenda.auth.exception.MemberNotFoundException;
import tcp.project.agenda.auth.exception.NotAuthorizedMemberException;
import tcp.project.agenda.auth.infrastructure.AuthorizationExtractor;
import tcp.project.agenda.auth.infrastructure.JwtTokenProvider;
import tcp.project.agenda.member.domain.Member;
import tcp.project.agenda.member.domain.MemberRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static tcp.project.agenda.auth.ui.AuthenticationArgumentResolver.AUTHENTICATED_KEY;
import static tcp.project.agenda.member.domain.Grade.GENERAL;
import static tcp.project.agenda.member.domain.Grade.REPRESENTATIVE;

@Component
@RequiredArgsConstructor
public class RepresentationAuthInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String accessToken = AuthorizationExtractor.extract(authHeader);
        Long memberId = tokenProvider.getMemberId(accessToken);

        Member member = getMember(memberId);
        validateMemberGrade(member);

        request.setAttribute(AUTHENTICATED_KEY, memberId);
        return true;
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(String.valueOf(memberId)));
    }

    private void validateMemberGrade(Member member) {
        if (!REPRESENTATIVE.equals(member.getGrade())) {
            throw new NotAuthorizedMemberException();
        }
    }
}