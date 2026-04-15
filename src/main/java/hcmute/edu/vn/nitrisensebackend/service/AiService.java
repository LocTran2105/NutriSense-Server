package hcmute.edu.vn.nitrisensebackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.nitrisensebackend.dto.AiAnalyzeResult;
import hcmute.edu.vn.nitrisensebackend.dto.NutrientDto;
import hcmute.edu.vn.nitrisensebackend.entity.AiProcessingLog;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.FoodItem;
import hcmute.edu.vn.nitrisensebackend.entity.UserGoal;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.repository.AiProcessingLogRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    // Dùng chung 1 RestTemplate để không làm treo server (Constructor Injection)
    private final RestTemplate restTemplate;
    private final AiProcessingLogRepository aiProcessingLogRepository;
    private final FoodItemRepository foodItemRepository;

    @Value("${gemini.api.url}") private String apiUrl;
    @Value("${gemini.api.key}") private String apiKey;
    @Value("${deepseek.api.key}") private String deepseekApiKey;

    public AiService(RestTemplate restTemplate, AiProcessingLogRepository logRepo, FoodItemRepository itemRepo) {
        this.restTemplate = restTemplate;
        this.aiProcessingLogRepository = logRepo;
        this.foodItemRepository = itemRepo;
    }

    // ==========================================================
    // SCHEMA CHUNG
    // ==========================================================
    private Map<String, Object> getNutrientResponseSchema(boolean isArray) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("status", Map.of("type", "string", "description", "Chỉ được trả 'success' hoặc 'ask_user'"));
        properties.put("food_name", Map.of("type", "string", "description", "Tên món ăn. Nếu status là ask_user thì để rỗng"));
        properties.put("calories", Map.of("type", "number"));
        properties.put("protein_g", Map.of("type", "number"));
        properties.put("carbs_g", Map.of("type", "number"));
        properties.put("fat_g", Map.of("type", "number"));
        properties.put("fiber_g", Map.of("type", "number"));
        properties.put("vitamin_a_mcg", Map.of("type", "number"));
        properties.put("vitamin_b12_mcg", Map.of("type", "number"));
        properties.put("vitamin_c_mg", Map.of("type", "number"));
        properties.put("vitamin_d_mcg", Map.of("type", "number"));
        properties.put("iron_mg", Map.of("type", "number"));
        properties.put("calcium_mg", Map.of("type", "number"));
        properties.put("potassium_mg", Map.of("type", "number"));
        properties.put("question", Map.of("type", "string", "description", "Câu hỏi ngược lại cho user"));

        List<String> requiredFields = Arrays.asList(
                "status", "food_name", "calories", "protein_g", "carbs_g", "fat_g",
                "fiber_g", "vitamin_a_mcg", "vitamin_b12_mcg", "vitamin_c_mg",
                "vitamin_d_mcg", "iron_mg", "calcium_mg", "potassium_mg", "question"
        );

        Map<String, Object> schema = new HashMap<>();
        if (isArray) {
            schema.put("type", "array");
            schema.put("items", Map.of("type", "object", "properties", properties, "required", requiredFields));
        } else {
            schema.put("type", "object");
            schema.put("properties", properties);
            schema.put("required", requiredFields);
        }
        return schema;
    }

    // ==========================================================
    // 1. NHẬN DIỆN TEXT (HỖ TRỢ NHIỀU MÓN)
    // ==========================================================
    public AiAnalyzeResult analyzeFood(Long userId, String userInput) {
        String cleanJson = "[]";
        try {
            String instructionText =
                    "Bạn là chuyên gia dinh dưỡng ẩm thực. Phân tích TẤT CẢ món ăn người dùng nhập vào. BẮT BUỘC trả về MẢNG JSON tuân thủ tuyệt đối schema.\n" +
                            "QUY TẮC XỬ LÝ:\n" +
                            "1. Phân tách từng món riêng biệt. LUÔN tự giả định khẩu phần trung bình. TUYỆT ĐỐI KHÔNG hỏi về số lượng.\n" +
                            "2. BẮT BUỘC ask_user NẾU input không phải đồ ăn. KHÔNG ask_user nếu có thể suy luận món phổ biến.\n" +
                            "3. Phải tính toán kỹ lưỡng vi chất. KHÔNG gán 0.0 nếu món đó thực tế có chứa chất đó.";

            // Đổi tham số cuối thành TRUE để ép AI trả về mảng
            Map<String, Object> requestBody = createGeminiRestRequest(instructionText, userInput, null, true);

            ResponseEntity<String> response = sendRequestToGemini(requestBody);
            cleanJson = parseSafe(response.getBody());

            // Đọc kết quả thành List
            List<NutrientDto> dtos = mapper.readValue(cleanJson, new TypeReference<List<NutrientDto>>() {});
            saveLog(userId, "text", userInput, cleanJson, null);

            AiAnalyzeResult result = new AiAnalyzeResult();
            if (!dtos.isEmpty() && "ask_user".equals(dtos.get(0).getStatus())) {
                result.setAskUser(true);
                result.setQuestion(dtos.get(0).getQuestion());
            } else {
                result.setAskUser(false);
                List<FoodItem> foodItems = new ArrayList<>();
                for (NutrientDto dto : dtos) {
                    if ("success".equals(dto.getStatus())) {
                        foodItems.add(mapDtoToFoodItem(dto, userId));
                    }
                }
                result.setFoodItems(foodItems);
            }
            return result;
        } catch (HttpStatusCodeException e) {
            logger.error("========== GOOGLE API ERROR (TEXT) ==========\n{}", e.getResponseBodyAsString());
            saveLog(userId, "text", userInput, cleanJson, "Google Error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Google API Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Lỗi AI Text: {}", e.getMessage());
            saveLog(userId, "text", userInput, cleanJson, "Lỗi Parse: " + e.getMessage());
            throw new RuntimeException("Lỗi AI Text: " + e.getMessage());
        }
    }

    // ==========================================================
    // 2. NHẬN DIỆN ẢNH
    // ==========================================================
    public List<NutrientDto> analyzeFoodImage(Long userId, MultipartFile file) {
        String cleanJson = "[]";
        String originalFilename = file != null ? file.getOriginalFilename() : "unknown_image";

        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes()).replaceAll("\\s", "");
            String instructionImage =
                    "Bạn là chuyên gia dinh dưỡng ẩm thực. Nhận diện TẤT CẢ món ăn trong ảnh. BẮT BUỘC trả về mảng JSON tuân thủ tuyệt đối schema.\n" +
                            "QUY TẮC: Phân tách từng món. Tự ước lượng khẩu phần. TUYỆT ĐỐI KHÔNG hỏi về số lượng. Tính vi chất đầy đủ.";

            Map<String, Object> requestBody = createGeminiRestRequest(instructionImage, null, base64Image, true);
            ResponseEntity<String> response = sendRequestToGemini(requestBody);

            cleanJson = parseSafe(response.getBody());
            List<NutrientDto> result = mapper.readValue(cleanJson, new TypeReference<List<NutrientDto>>() {});
            saveLog(userId, "image", "Ảnh: " + originalFilename, cleanJson, null);
            return result;
        } catch (HttpStatusCodeException e) {
            logger.error("========== GOOGLE API ERROR (IMAGE) ==========\n{}", e.getResponseBodyAsString());
            saveLog(userId, "image", "Ảnh: " + originalFilename, cleanJson, "Google Error");
            throw new RuntimeException("Google API Error");
        } catch (Exception e) {
            logger.error("Lỗi AI Ảnh: {}", e.getMessage());
            saveLog(userId, "image", "Ảnh: " + originalFilename, cleanJson, e.getMessage());
            throw new RuntimeException("Lỗi AI Ảnh: " + e.getMessage());
        }
    }

    // ==========================================================
    // 3. TẠO CÂU NHẮC NHỞ DINH DƯỠNG CUỐI NGÀY (CÓ DEEPSEEK FALLBACK)
    // ==========================================================
    public String generateDailyReminder(Long userId, List<UserGoal> goals, List<FoodEntryItem> todayItems, DailySummary summary) {
        final String FALLBACK_MESSAGE = "Hôm nay có vẻ chưa cân bằng lắm, mai hãy chú ý lượng Calo và nạp đủ nước nhé!";
        try {
            double totalVitC = 0, totalIron = 0, totalCalcium = 0;
            for (FoodEntryItem item : todayItems) {
                // ĐÃ FIX: Thêm .doubleValue() và đổi 0 thành 0.0
                totalVitC += item.getVitaminCMg() != null ? item.getVitaminCMg().doubleValue() : 0.0;
                totalIron += item.getIronMg() != null ? item.getIronMg().doubleValue() : 0.0;
                totalCalcium += item.getCalciumMg() != null ? item.getCalciumMg().doubleValue() : 0.0;
            }

            double targetCal = 2000.0, targetWater = 2000.0, targetPro = 150.0;
            if (goals != null) {
                for (UserGoal g : goals) {
                    if (g.getTargetValue() == null) continue;
                    String type = g.getGoalType().toLowerCase();
                    if (type.contains("calorie") || type.contains("calo")) {
                        targetCal = g.getTargetValue().doubleValue();
                        targetPro = (targetCal * 0.30) / 4.0;
                    } else if (type.contains("water") || type.contains("nước")) {
                        targetWater = g.getTargetValue().doubleValue();
                    } else if (type.contains("protein")) {
                        targetPro = g.getTargetValue().doubleValue();
                    }
                }
            }
            double targetVitC = 90.0, targetIron = 18.0, targetCalcium = 1000.0;

            double actualCal = (summary != null && summary.getTotalCalories() != null) ? summary.getTotalCalories().doubleValue() : 0.0;
            double actualPro = (summary != null && summary.getTotalProteinG() != null) ? summary.getTotalProteinG().doubleValue() : 0.0;
            double actualWater = (summary != null && summary.getTotalWaterMl() != null) ? summary.getTotalWaterMl().doubleValue() : 0.0;
            List<String> warnings = new ArrayList<>();
            if (actualCal > targetCal * 1.10) warnings.add("Dư thừa " + (int)(actualCal - targetCal) + " Calo");
            if (actualPro < targetPro * 0.90) warnings.add("Thiếu Protein");
            if (actualWater < targetWater * 0.90) warnings.add("Thiếu Nước");
            if (totalVitC < targetVitC * 0.90) warnings.add("Thiếu Vitamin C");
            if (totalIron < targetIron * 0.90) warnings.add("Thiếu Sắt");
            if (totalCalcium < targetCalcium * 0.90) warnings.add("Thiếu Canxi");

            if (warnings.isEmpty()) {
                return "Tuyệt vời! Hôm nay bạn nạp dinh dưỡng rất chuẩn. Mai cứ giữ nhịp điệu này nhé! 🎉";
            }

            String prompt = "Người dùng hôm nay gặp vấn đề: " + String.join(", ", warnings) + ". \n" +
                    "Nhiệm vụ: Viết đúng 1 câu duy nhất (dưới 25 chữ), cảnh báo nhẹ nhàng. Đưa ra 1 giải pháp thực tế cho ngày mai.";

            try {
                // TẦNG 1: GỌI GEMINI
                Map<String, Object> requestBody = createSimpleRequest(prompt);
                ResponseEntity<String> response = sendRequestToGemini(requestBody);
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    return candidates.get(0).path("content").path("parts").get(0).path("text").asText().trim();
                }
                throw new RuntimeException("Gemini trả về mảng rỗng");

            } catch (ResourceAccessException rae) {
                logger.warn("[Gemini] Lỗi mạng/Timeout. Chuyển DeepSeek.");
                return executeDeepSeekFallback(prompt, FALLBACK_MESSAGE);
            } catch (HttpStatusCodeException httpEx) {
                if (httpEx.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || httpEx.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    logger.warn("[Gemini] Quá tải 503/429. Chuyển DeepSeek.");
                    return executeDeepSeekFallback(prompt, FALLBACK_MESSAGE);
                }
                logger.error("[Gemini] Lỗi HTTP: {}", httpEx.getResponseBodyAsString());
                return FALLBACK_MESSAGE;
            }
        } catch (Exception e) {
            logger.error("[Hệ thống] Lỗi Logic: {}", e.getMessage(), e);
            return FALLBACK_MESSAGE;
        }
    }

    private String executeDeepSeekFallback(String prompt, String fallbackMessage) {
        try {
            return callDeepSeek(prompt);
        } catch (Exception ex) {
            logger.error("[DeepSeek] Chữa cháy thất bại: {}", ex.getMessage());
            return fallbackMessage;
        }
    }

    // ==========================================================
    // UTILS: BUILD REQUEST & PARSE CHUNG
    // ==========================================================
    private ResponseEntity<String> sendRequestToGemini(Map<String, Object> requestBody) {
        String urlWithKey = apiUrl + "?key=" + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(urlWithKey, HttpMethod.POST, entity, String.class);
    }

    private Map<String, Object> createGeminiRestRequest(String instruction, String text, String base64, boolean isArray) {
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", instruction + (text != null ? " Người dùng nhập: " + text : "")));

        if (base64 != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("mime_type", "image/jpeg");
            data.put("data", base64);
            parts.add(Map.of("inline_data", data));
        }

        Map<String, Object> genConfig = new HashMap<>();
        genConfig.put("response_mime_type", "application/json");
        genConfig.put("response_schema", getNutrientResponseSchema(isArray));

        return Map.of("contents", Collections.singletonList(Map.of("parts", parts)), "generationConfig", genConfig);
    }

    private Map<String, Object> createSimpleRequest(String prompt) {
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("text", prompt));
        Map<String, Object> genConfig = new HashMap<>();
        genConfig.put("response_mime_type", "text/plain");
        return Map.of("contents", Collections.singletonList(Map.of("parts", parts)), "generationConfig", genConfig);
    }

    private String callDeepSeek(String prompt) throws Exception {
        String url = "https://api.deepseek.com/v1/chat/completions";
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("messages", Collections.singletonList(Map.of("role", "user", "content", prompt)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepseekApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        JsonNode root = mapper.readTree(response.getBody());
        JsonNode choices = root.path("choices");
        if (choices.isArray() && !choices.isEmpty()) {
            return choices.get(0).path("message").path("content").asText().trim();
        }
        throw new RuntimeException("DeepSeek trả về JSON rỗng");
    }

    private String parseSafe(String body) throws Exception {
        JsonNode root = mapper.readTree(body);
        JsonNode candidates = root.path("candidates");
        if (candidates.isMissingNode() || candidates.isEmpty()) {
            throw new RuntimeException("Gemini từ chối phản hồi");
        }
        String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText().trim();
        return text.replaceAll("^```json\\s*", "").replaceAll("```$", "").trim();
    }

    private void saveLog(Long userId, String type, String input, String json, String error) {
        AiProcessingLog log = new AiProcessingLog();
        log.setUserId(userId);
        log.setInputType(type);
        log.setRawInput(input);
        log.setAiResponseJson((json == null || json.trim().isEmpty()) ? "[]" : json);
        log.setErrorMessage(error);
        aiProcessingLogRepository.save(log);
    }

    private FoodItem mapDtoToFoodItem(NutrientDto dto, Long userId) {
        FoodItem item = new FoodItem();
        item.setName(dto.getFoodName());
        item.setCalories(safeBigDecimal(dto.getCalories()));
        item.setProteinG(safeBigDecimal(dto.getProteinG()));
        item.setCarbsG(safeBigDecimal(dto.getCarbsG()));
        item.setFatG(safeBigDecimal(dto.getFatG()));
        item.setFiberG(safeBigDecimal(dto.getFiberG()));
        item.setVitaminAMcg(safeBigDecimal(dto.getVitaminAMcg()));
        item.setVitaminB12Mcg(safeBigDecimal(dto.getVitaminB12Mcg()));
        item.setVitaminCMg(safeBigDecimal(dto.getVitaminCMg()));
        item.setVitaminDMcg(safeBigDecimal(dto.getVitaminDMcg()));
        item.setIronMg(safeBigDecimal(dto.getIronMg()));
        item.setCalciumMg(safeBigDecimal(dto.getCalciumMg()));
        item.setPotassiumMg(safeBigDecimal(dto.getPotassiumMg()));
        item.setSource("gemini_ai");
        item.setCreatedBy(userId);
        item.setServingSize(BigDecimal.ONE);
        item.setServingUnit("phần");
        return item;
    }

    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}