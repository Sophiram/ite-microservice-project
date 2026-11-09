package kh.edu.istad.iam.web;

import java.util.Locale;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration(proxyBeanMethods = false)
public class IamWebConfiguration implements WebMvcConfigurer {
    private static final Set<String> SUPPORTED = Set.of("en", "km");
    private final LocaleResolver localeResolver;

    public IamWebConfiguration(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Bean
    static LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                                     jakarta.servlet.http.HttpServletResponse response, Object handler) {
                String language = request.getParameter("lang");
                if (language != null && SUPPORTED.contains(language.toLowerCase(Locale.ROOT))) {
                    localeResolver.setLocale(request, response, Locale.forLanguageTag(language));
                }
                return true;
            }
        });
    }
}
