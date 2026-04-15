package hcmute.edu.vn.nitrisensebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.url}") private String apiUrl;
    @Value("${gemini.api.key}") private String apiKey;

    // Inject từ Spring Boot
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    public GeminiService(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public String generateChatResponse(String systemInstruction, String userText) throws Exception {
        Map<String, Object> schemaProps = new LinkedHashMap<>();
        // Đã cập nhật đầy đủ các loại Enum theo chuẩn DB
        schemaProps.put("message_type", Map.of(
                "type", "string",
                "description", "Chỉ chọn: CHAT, FOLLOWUP, REMINDER, WARNING, ACHIEVEMENT, SYSTEM_NOTICE"
        ));
        schemaProps.put("response_text", Map.of("type", "string"));
        schemaProps.put("context_data", Map.of("type", "object"));

        Map<String, Object> responseSchema = Map.of(
                "type", "object",
                "properties", schemaProps,
                "required", Arrays.asList("message_type", "response_text")
        );

        Map<String, Object> textPart = Map.of("text", systemInstruction + "\nUser nói: " + userText);
        Map<String, Object> content = Map.of("parts", Collections.singletonList(textPart));

        Map<String, Object> genConfig = new HashMap<>();
        genConfig.put("response_mime_type", "application/json");
        genConfig.put("response_schema", responseSchema);

        Map<String, Object> requestBody = Map.of("contents", Collections.singletonList(content), "generationConfig", genConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + "?key=" + apiKey, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);

        JsonNode root = mapper.readTree(response.getBody());
        JsonNode candidates = root.path("candidates");

        // Bảo vệ chống NullPointer khi API trả sai cấu trúc
        if (candidates.isMissingNode() || candidates.isEmpty()) {
            throw new RuntimeException("Gemini từ chối phản hồi hoặc block an toàn.");
        }

        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (parts.isMissingNode() || parts.isEmpty()) {
            throw new RuntimeException("Gemini trả về response mất cấu trúc parts.");
        }

        return parts.get(0).path("text").asText().trim();
    }
}