package hcmute.edu.vn.nitrisensebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();

        // Thời gian tối đa để kết nối mạng: 5000 mili-giây (5 giây)
        factory.setConnectTimeout(5000);

        // Thời gian tối đa đợi AI suy nghĩ trả lời: 15000 mili-giây (15 giây)
        factory.setReadTimeout(15000);

        return new RestTemplate(factory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}