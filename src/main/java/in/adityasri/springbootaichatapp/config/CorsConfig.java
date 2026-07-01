package in.adityasri.springbootaichatapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * — CORS (Cross-Origin Resource Sharing):
 *   Browsers block JavaScript on page A from calling API B unless B opts in.
 *   A separately-hosted frontend (e.g. a React app on :3000) would otherwise be
 *   unable to call these endpoints. This opens /api/** to GET/POST from any
 *   origin — fine for a demo; tighten `allowedOrigins` for production.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}
