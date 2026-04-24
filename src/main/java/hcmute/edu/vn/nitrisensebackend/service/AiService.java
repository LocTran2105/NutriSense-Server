package hcmute.edu.vn.nitrisensebackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.nitrisensebackend.dto.AiAnalyzeResult;
import hcmute.edu.vn.nitrisensebackend.dto.NutrientDto;
import hcmute.edu.vn.nitrisensebackend.dto.ReminderResponseDto;
import hcmute.edu.vn.nitrisensebackend.entity.AiProcessingLog;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.FoodItem;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.repository.AiProcessingLogRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodItemRepository;
import hcmute.edu.vn.nitrisensebackend.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final AiProcessingLogRepository aiProcessingLogRepository;
    private final FoodItemRepository foodItemRepository;

    @Value("${gemini.api.url}") private String apiUrl;
    @Value("${gemini.api.key}") private String apiKey;
    @Value("${gemini.api.key.fallback}") private String fallbackKey;

    public AiService(RestTemplate restTemplate, AiProcessingLogRepository logRepo, FoodItemRepository itemRepo) {
        this.restTemplate = restTemplate;
        this.aiProcessingLogRepository = logRepo;
        this.foodItemRepository = itemRepo;
    }

    private Map<String, Object> getNutrientResponseSchema(boolean isArray) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("status", Map.of("type", "STRING", "description", "Chỉ được trả 'success' hoặc 'ask_user'"));
        properties.put("food_name", Map.of("type", "STRING", "description", "Tên món ăn. Nếu status là ask_user thì để rỗng"));
        properties.put("calories", Map.of("type", "NUMBER"));
        properties.put("protein_g", Map.of("type", "NUMBER"));
        properties.put("carbs_g", Map.of("type", "NUMBER"));
        properties.put("fat_g", Map.of("type", "NUMBER"));
        properties.put("fiber_g", Map.of("type", "NUMBER"));
        properties.put("vitamin_a_mcg", Map.of("type", "NUMBER"));
        properties.put("vitamin_b12_mcg", Map.of("type", "NUMBER"));
        properties.put("vitamin_c_mg", Map.of("type", "NUMBER"));
        properties.put("vitamin_d_mcg", Map.of("type", "NUMBER"));
        properties.put("iron_mg", Map.of("type", "NUMBER"));
        properties.put("calcium_mg", Map.of("type", "NUMBER"));
        properties.put("potassium_mg", Map.of("type", "NUMBER"));
        properties.put("question", Map.of("type", "STRING", "description", "Câu hỏi ngược lại cho user"));

        List<String> requiredFields = Arrays.asList(
                "status", "food_name", "calories", "protein_g", "carbs_g", "fat_g",
                "fiber_g", "vitamin_a_mcg", "vitamin_b12_mcg", "vitamin_c_mg",
                "vitamin_d_mcg", "iron_mg", "calcium_mg", "potassium_mg", "question"
        );

        Map<String, Object> schema = new HashMap<>();
        if (isArray) {
            schema.put("type", "ARRAY");
            schema.put("items", Map.of("type", "OBJECT", "properties", properties, "required", requiredFields));
        } else {
            schema.put("type", "OBJECT");
            schema.put("properties", properties);
            schema.put("required", requiredFields);
        }
        return schema;
    }

    public AiAnalyzeResult analyzeFood(Long userId, String userInput) {
        String cleanJson = "[]";
        try {
            String instructionText =
                    "Bạn là chuyên gia dinh dưỡng ẩm thực. Phân tích TẤT CẢ món ăn người dùng nhập vào. BẮT BUỘC trả về MẢNG JSON tuân thủ tuyệt đối schema.\n" +
                            "QUY TẮC XỬ LÝ:\n" +
                            "1. Phân tách từng món riêng biệt. LUÔN tự giả định khẩu phần trung bình. TUYỆT ĐỐI KHÔNG hỏi về số lượng.\n" +
                            "2. BẮT BUỘC ask_user NẾU input không phải đồ ăn. KHÔNG ask_user nếu có thể suy luận món phổ biến.\n" +
                            "3. Phải tính toán kỹ lưỡng vi chất. KHÔNG gán 0.0 nếu món đó thực tế có chứa chất đó.";

            Map<String, Object> requestBody = createGeminiRestRequest(instructionText, userInput, null, true);

            ResponseEntity<String> response;
            try {
                response = sendRequestToGemini(requestBody, false);
            } catch (Exception e1) {
                logger.warn("[AiService] Key 1 lỗi/quá tải (Text). Đang thử Key 2...");
                try {
                    response = sendRequestToGemini(requestBody, true);
                } catch (Exception e2) {
                    throw new RuntimeException("Cả 2 Key Gemini đều quá tải: " + e2.getMessage());
                }
            }

            cleanJson = parseSafe(response.getBody());

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

    public List<NutrientDto> analyzeFoodImage(Long userId, MultipartFile file) {
        String cleanJson = "[]";
        String originalFilename = file != null ? file.getOriginalFilename() : "unknown_image";

        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes()).replaceAll("\\s", "");
            String instructionImage =
                    "Bạn là chuyên gia dinh dưỡng ẩm thực. Nhận diện TẤT CẢ món ăn trong ảnh. BẮT BUỘC trả về mảng JSON tuân thủ tuyệt đối schema.\n" +
                            "QUY TẮC: Phân tách từng món. Tự ước lượng khẩu phần. TUYỆT ĐỐI KHÔNG hỏi về số lượng. Tính vi chất đầy đủ.";

            Map<String, Object> requestBody = createGeminiRestRequest(instructionImage, null, base64Image, true);

            ResponseEntity<String> response;
            try {
                response = sendRequestToGemini(requestBody, false);
            } catch (Exception e1) {
                logger.warn("[AiService] Key 1 lỗi/quá tải (Image). Đang thử Key 2...");
                try {
                    response = sendRequestToGemini(requestBody, true);
                } catch (Exception e2) {
                    throw new RuntimeException("Cả 2 Key Gemini đều quá tải khi nhận diện ảnh");
                }
            }

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

    public ReminderResponseDto generateDailyReminder(Long userId, User profile, List<FoodEntryItem> todayItems, DailySummary summary) {
        List<FoodEntryItem> safeItems = todayItems != null ? todayItems : Collections.emptyList();

        double totalVitC = 0, totalIron = 0, totalCalcium = 0;
        for (FoodEntryItem item : safeItems) {
            totalVitC += item.getVitaminCMg() != null ? item.getVitaminCMg().doubleValue() : 0.0;
            totalIron += item.getIronMg() != null ? item.getIronMg().doubleValue() : 0.0;
            totalCalcium += item.getCalciumMg() != null ? item.getCalciumMg().doubleValue() : 0.0;
        }

        double targetCal = 2000.0, targetWater = 2000.0, targetPro = 150.0;

        if (profile != null) {
            if (profile.getDailyCalorieGoal() != null) {
                targetCal = profile.getDailyCalorieGoal().doubleValue();
                targetPro = (targetCal * 0.30) / 4.0;
            }
            if (profile.getWaterGoalMl() != null) {
                targetWater = profile.getWaterGoalMl().doubleValue();
            }
        }

        double targetVitC = 90.0, targetIron = 18.0, targetCalcium = 1000.0;

        double actualCal = (summary != null && summary.getTotalCalories() != null) ? summary.getTotalCalories().doubleValue() : 0.0;
        double actualPro = (summary != null && summary.getTotalProteinG() != null) ? summary.getTotalProteinG().doubleValue() : 0.0;
        double actualWater = (summary != null && summary.getTotalWaterMl() != null) ? summary.getTotalWaterMl().doubleValue() : 0.0;

        List<String> deficitLines = new ArrayList<>();
        List<String> foodTips = new ArrayList<>();

        Pageable topTwo = PageRequest.of(0, 2);

        if (actualCal < targetCal) {
            deficitLines.add("Thiếu " + fmt(targetCal - actualCal) + " kcal");
        }

        if (actualPro < targetPro) {
            deficitLines.add("Thiếu " + fmt(targetPro - actualPro) + "g Protein");
            List<String> foodNames = foodItemRepository.findTopProteinFoods(topTwo)
                    .stream().map(FoodItem::getName).collect(Collectors.toList());
            if (!foodNames.isEmpty()) foodTips.add("Protein: " + String.join(" hoặc ", foodNames));
        }

        if (actualWater < targetWater) {
            deficitLines.add("Thiếu " + fmt(targetWater - actualWater) + "ml Nước");
            foodTips.add("Nước: Nhớ uống đủ nước nhé!");
        }

        if (totalVitC < targetVitC) {
            deficitLines.add("Thiếu " + fmt(targetVitC - totalVitC) + "mg Vitamin C");
            List<String> foodNames = foodItemRepository.findTopVitaminCFoods(topTwo)
                    .stream().map(FoodItem::getName).collect(Collectors.toList());
            if (!foodNames.isEmpty()) foodTips.add("Vitamin C: " + String.join(" hoặc ", foodNames));
        }

        if (totalIron < targetIron) {
            deficitLines.add("Thiếu " + fmt(targetIron - totalIron) + "mg Sắt");
            List<String> foodNames = foodItemRepository.findTopIronFoods(topTwo)
                    .stream().map(FoodItem::getName).collect(Collectors.toList());
            if (!foodNames.isEmpty()) foodTips.add("Sắt: " + String.join(" hoặc ", foodNames));
        }

        if (totalCalcium < targetCalcium) {
            deficitLines.add("Thiếu " + fmt(targetCalcium - totalCalcium) + "mg Canxi");
            List<String> foodNames = foodItemRepository.findTopCalciumFoods(topTwo)
                    .stream().map(FoodItem::getName).collect(Collectors.toList());
            if (!foodNames.isEmpty()) foodTips.add("Canxi: " + String.join(" hoặc ", foodNames));
        }

        if (deficitLines.isEmpty()) {
            String okMsg = "Tuyệt vời! Hôm nay bạn đã đạt mục tiêu dinh dưỡng, cứ giữ nhịp này nhé.";
            return new ReminderResponseDto(okMsg, okMsg);
        }

        String notificationText = buildShortReminder(deficitLines);
        String detailText = buildFullReminder(deficitLines, foodTips);

        return new ReminderResponseDto(notificationText, detailText);
    }

    private String buildShortReminder(List<String> deficitLines) {
        return "Bạn đang thiếu: " + String.join("; ", deficitLines) + ".";
    }

    private String buildFullReminder(List<String> deficitLines, List<String> foodTips) {
        String summary = String.join(", ", deficitLines);
        StringBuilder sb = new StringBuilder();

        sb.append("Bạn đang ").append(summary).append(".\n");

        if (!foodTips.isEmpty()) {
            sb.append("\n💡 Gợi ý để bù chất:\n- ");
            sb.append(String.join("\n- ", foodTips));
        }
        return sb.toString();
    }

    private String fmt(double value) {
        return BigDecimal.valueOf(value).setScale(1, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private ResponseEntity<String> sendRequestToGemini(Map<String, Object> requestBody, boolean useFallback) {
        String currentKey = useFallback ? fallbackKey : apiKey;
        String urlWithKey = apiUrl + "?key=" + currentKey;

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