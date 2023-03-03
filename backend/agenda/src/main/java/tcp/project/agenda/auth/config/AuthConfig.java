package tcp.project.agenda.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tcp.project.agenda.auth.ui.AuthenticationArgumentResolver;
import tcp.project.agenda.auth.ui.interceptor.BasicAuthInterceptor;
import tcp.project.agenda.auth.ui.interceptor.RepresentationAuthInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final BasicAuthInterceptor basicAuthInterceptor;
    private final RepresentationAuthInterceptor representationAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(basicAuthInterceptor);
        registry.addInterceptor(representationAuthInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationArgumentResolver());
    }
}
