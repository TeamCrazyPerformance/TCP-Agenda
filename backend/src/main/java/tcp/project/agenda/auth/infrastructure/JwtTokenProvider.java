package tcp.project.agenda.auth.infrastructure;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tcp.project.agenda.auth.exception.ExpiredTokenException;
import tcp.project.agenda.auth.exception.InvalidAccessTokenException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expiredMillSecond;

    public JwtTokenProvider(
            @Value("${security.jwt.token.secret-key}") String key,
            @Value("${security.jwt.token.expired}") long expiredMillSecond) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.expiredMillSecond = expiredMillSecond;
    }

    public String createToken(String payload) {
        Date currentTime = new Date();
        long expiredTime = currentTime.getTime() + expiredMillSecond;

        return Jwts.builder()
                .setIssuedAt(currentTime)
                .setExpiration(new Date(expiredTime))
                .setSubject(payload)
                .signWith(key)
                .compact();
    }

    public Long getMemberId(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();

            String memberId = parser.parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return Long.parseLong(memberId);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidAccessTokenException();
        }
    }
}
