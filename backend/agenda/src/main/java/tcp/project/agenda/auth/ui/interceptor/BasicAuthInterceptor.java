package tcp.project.agenda.auth.ui.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tcp.project.agenda.auth.infrastructure.AuthorizationExtractor;
import tcp.project.agenda.auth.infrastructure.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static tcp.project.agenda.auth.ui.AuthenticationArgumentResolver.AUTHENTICATED_KEY;

@Component
@RequiredArgsConstructor
public class BasicAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String accessToken = AuthorizationExtractor.extract(authHeader);
        Long memberId = tokenProvider.getMemberId(accessToken);

        request.setAttribute(AUTHENTICATED_KEY, memberId);
        return true;
    }
}
