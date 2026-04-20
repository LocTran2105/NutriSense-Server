package hcmute.edu.vn.nitrisensebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.nitrisensebackend.dto.ChatMessageResponseDTO;
import hcmute.edu.vn.nitrisensebackend.entity.ChatMessage;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.entity.ExerciseTest;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.User;
import hcmute.edu.vn.nitrisensebackend.enums.ChatMessageType;
import hcmute.edu.vn.nitrisensebackend.enums.ChatSender;
import hcmute.edu.vn.nitrisensebackend.repository.ChatMessageRepository;
import hcmute.edu.vn.nitrisensebackend.repository.ExerciseTestRepository;
import hcmute.edu.vn.nitrisensebackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final GeminiService geminiService;
    private final DailySummaryService dailySummaryService;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final MealService mealService;
    private final ExerciseTestRepository exerciseTestRepository;

    public ChatService(ChatMessageRepository chatRepository,
                       GeminiService geminiService,
                       DailySummaryService dailySummaryService,
                       ObjectMapper mapper,
                       UserRepository userRepository,
                       MealService mealService,
                       ExerciseTestRepository exerciseTestRepository) {
        this.chatRepository = chatRepository;
        this.geminiService = geminiService;
        this.dailySummaryService = dailySummaryService;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.mealService = mealService;
        this.exerciseTestRepository = exerciseTestRepository;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDTO> getChatHistory(Long userId) {
        return chatRepository.findByUserIdOrderByCreatedAtAsc(userId).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadBadgeCount(Long userId) {
        return chatRepository.countByUserIdAndIsReadFalseAndSenderIn(userId, Arrays.asList(ChatSender.AI, ChatSender.SYSTEM));
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        chatRepository.markUnreadAsRead(userId, Arrays.asList(ChatSender.AI, ChatSender.SYSTEM));
    }

    @Transactional
    public ChatMessageResponseDTO processUserMessage(Long userId, String userText) {

        // 1. BUILD RAG CONTEXT
        StringBuilder contextBuilder = new StringBuilder("Ngữ cảnh hiện tại của User:\n");
        User user = userRepository.findById(userId).orElse(null);

        // BƠM THÔNG TIN CƠ BẢN
        if (user != null && user.getWeightKg() != null && user.getHeightCm() != null) {
            contextBuilder.append(String.format("- Chỉ số cơ thể: Cân nặng %.1f kg, Chiều cao %.1f cm.\n",
                    user.getWeightKg(), user.getHeightCm()));
        }

        // BƠM THÔNG TIN THIẾU HỤT
        String deficitContext = buildDailyNutrientDeficitContext(userId, user);
        contextBuilder.append(deficitContext);

        // BƠM THÔNG TIN THỂ LỰC
        try {
            List<ExerciseTest> recentTests = exerciseTestRepository.findTop10ByUserIdOrderByTestDateDescTestIdDesc(userId);
            if (recentTests != null && !recentTests.isEmpty()) {
                contextBuilder.append("- Đánh giá thể lực gần đây của user:\n");
                for (ExerciseTest test : recentTests) {
                    String testName = test.getTestType();
                    // Việt hóa tên bài tập
                    if ("pushups".equals(testName)) testName = "Hít đất";
                    else if ("plank_seconds".equals(testName)) testName = "Plank";
                    else if ("situps".equals(testName)) testName = "Gập bụng";
                    else if ("running_1km".equals(testName)) testName = "Chạy 1km";

                    String notes = test.getNotes() != null ? test.getNotes() : "Chưa có đánh giá";
                    contextBuilder.append(String.format("  + Ngày %s: %s đạt %s %s (Đánh giá: %s)\n",
                            test.getTestDate(), testName, test.getValue(), test.getUnit(), notes));
                }
            }
        } catch (Exception ignored) {
            System.err.println("[ChatService] Lỗi lấy dữ liệu thể lực: " + ignored.getMessage());
        }

        // BƠM LỊCH SỬ CHAT
        try {
            List<ChatMessage> recentChats = chatRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
            Collections.reverse(recentChats);
            contextBuilder.append("- Lịch sử chat gần đây:\n");
            for (ChatMessage msg : recentChats) {
                contextBuilder.append("  + ").append(msg.getSender().name()).append(": ").append(msg.getMessageText()).append("\n");
            }
        } catch (Exception ignored) {}

        // 2. LƯU TIN NHẮN USER XUỐNG DB
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setSender(ChatSender.USER);
        userMsg.setMessageType(ChatMessageType.CHAT);
        userMsg.setMessageText(userText);
        userMsg.setIsRead(true);
        chatRepository.save(userMsg);

        // 3. PROMPT ENGINEERING
        String systemPrompt = contextBuilder.toString() + "\n\n" +
                "Bạn là trợ lý dinh dưỡng và sức khỏe NitriSense.\n" +
                "QUY TẮC:\n" +
                "- Dựa vào phần '- THIẾU HỤT HÔM NAY (ĐÃ TÍNH SẴN)' để trả lời chính xác người dùng còn thiếu bao nhiêu chất.\n" +
                "- Dựa vào phần đánh giá thể lực để tư vấn thêm về chế độ ăn hoặc tập luyện nếu cần.\n" +
                "- KHÔNG TỰ TÍNH TOÁN LẠI các số liệu thiếu hụt đã cung cấp.\n" +
                "- Trả lời ngắn gọn, thân thiện, mang tính động viên.\n" +
                "BẮT BUỘC TRẢ VỀ ĐỊNH DẠNG JSON. KHÔNG KÈM MARKDOWN HAY TEXT THỪA.\n" +
                "Các giá trị hợp lệ cho message_type là: CHAT, WARNING, ACHIEVEMENT.\n" +
                "Mẫu JSON chuẩn:\n" +
                "{\n" +
                "  \"response_text\": \"Câu trả lời của bạn\",\n" +
                "  \"message_type\": \"CHAT\",\n" +
                "  \"context_data\": {}\n" +
                "}";

        // 4. GỌI AI VÀ XỬ LÝ FALLBACK (2 KEY GEMINI)
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setSender(ChatSender.AI);
        aiMsg.setIsRead(false);

        String aiJson = null;
        try {
            // Thử bằng Key chính (useFallback = false)
            aiJson = geminiService.generateChatResponse(systemPrompt, userText, false);
        } catch (Exception e) {
            System.err.println("[ChatService] Gemini lỗi/quá tải (Key 1). Đang chuyển sang Key 2...");
            try {
                // Thử bằng Key dự phòng (useFallback = true)
                aiJson = geminiService.generateChatResponse(systemPrompt, userText, true);
                System.out.println("[ChatService] Key Gemini 2 đã cứu cánh thành công!");
            } catch (Exception fallbackEx) {
                System.err.println("[ChatService] Cả 2 Key Gemini đều quá tải: " + fallbackEx.getMessage());
            }
        }

        if (aiJson != null) {
            try {
                // Chuẩn hóa chuỗi JSON cực kỳ an toàn
                String cleanJson = aiJson.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("```$", "").trim();

                JsonNode root = mapper.readTree(cleanJson);

                aiMsg.setMessageText(root.path("response_text").asText());
                aiMsg.setMessageType(safeParseEnum(root.path("message_type").asText("CHAT")));

                if (root.has("context_data") && !root.get("context_data").isNull() && !root.get("context_data").isEmpty()) {
                    aiMsg.setContextData(root.get("context_data"));
                }
            } catch (Exception parseEx) {
                System.err.println("[ChatService] Lỗi Parse JSON: " + parseEx.getMessage());
                aiMsg.setMessageText(aiJson); // Lưu text thuần nếu bể định dạng
                aiMsg.setMessageType(ChatMessageType.CHAT);
            }
        } else {
            aiMsg.setMessageType(ChatMessageType.WARNING);
            aiMsg.setMessageText("Hệ thống AI đang quá tải ở cả 2 kênh dự phòng. Bạn vui lòng thử lại sau ít phút nhé!");
        }

        return convertToDTO(chatRepository.save(aiMsg));
    }

    // ========================================================
    // HÀM BACKEND TỰ TÍNH TOÁN THIẾU HỤT
    // ========================================================
    private String buildDailyNutrientDeficitContext(Long userId, User user) {
        StringBuilder sb = new StringBuilder();

        double targetCal = 2000.0, targetWater = 2000.0, targetPro = 150.0;
        if (user != null) {
            if (user.getDailyCalorieGoal() != null) {
                targetCal = user.getDailyCalorieGoal().doubleValue();
                targetPro = (targetCal * 0.30) / 4.0; // 30% Calo từ Protein
            }
            if (user.getWaterGoalMl() != null) {
                targetWater = user.getWaterGoalMl().doubleValue();
            }
        }
        double targetVitC = 90.0, targetIron = 18.0, targetCalcium = 1000.0;

        double actualCal = 0, actualPro = 0, actualWater = 0;
        double actualVitC = 0, actualIron = 0, actualCalcium = 0;

        try {
            DailySummary summary = dailySummaryService.getSummaryByDate(userId, LocalDate.now());
            if (summary != null) {
                actualCal = summary.getTotalCalories() != null ? summary.getTotalCalories().doubleValue() : 0;
                actualPro = summary.getTotalProteinG() != null ? summary.getTotalProteinG().doubleValue() : 0;
                actualWater = summary.getTotalWaterMl() != null ? summary.getTotalWaterMl().doubleValue() : 0;
            }

            List<FoodEntryItem> todayItems = mealService.getMealItemsForWholeDay(userId, LocalDate.now());
            if (todayItems != null) {
                for (FoodEntryItem item : todayItems) {
                    actualVitC += item.getVitaminCMg() != null ? item.getVitaminCMg().doubleValue() : 0.0;
                    actualIron += item.getIronMg() != null ? item.getIronMg().doubleValue() : 0.0;
                    actualCalcium += item.getCalciumMg() != null ? item.getCalciumMg().doubleValue() : 0.0;
                }
            }
        } catch (Exception e) {
            System.err.println("[ChatService] Lỗi tính tổng dinh dưỡng: " + e.getMessage());
        }

        double missingCal = Math.max(0, targetCal - actualCal);
        double missingPro = Math.max(0, targetPro - actualPro);
        double missingWater = Math.max(0, targetWater - actualWater);
        double missingVitC = Math.max(0, targetVitC - actualVitC);
        double missingIron = Math.max(0, targetIron - actualIron);
        double missingCalcium = Math.max(0, targetCalcium - actualCalcium);

        sb.append("- THIẾU HỤT HÔM NAY (ĐÃ TÍNH SẴN):\n");
        if (missingCal == 0 && missingPro == 0 && missingWater == 0 && missingVitC == 0 && missingIron == 0 && missingCalcium == 0) {
            sb.append("  + Tuyệt vời! Hôm nay người dùng đã đạt đủ 100% mục tiêu dinh dưỡng.\n");
        } else {
            if (missingCal > 0) sb.append(String.format("  + Calo còn thiếu: %s kcal\n", fmt(missingCal)));
            if (missingPro > 0) sb.append(String.format("  + Protein còn thiếu: %s g\n", fmt(missingPro)));
            if (missingWater > 0) sb.append(String.format("  + Nước còn thiếu: %s ml\n", fmt(missingWater)));
            if (missingVitC > 0) sb.append(String.format("  + Vitamin C còn thiếu: %s mg\n", fmt(missingVitC)));
            if (missingIron > 0) sb.append(String.format("  + Sắt còn thiếu: %s mg\n", fmt(missingIron)));
            if (missingCalcium > 0) sb.append(String.format("  + Canxi còn thiếu: %s mg\n", fmt(missingCalcium)));
        }

        return sb.toString();
    }

    private String fmt(double value) {
        return BigDecimal.valueOf(value).setScale(1, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private ChatMessageType safeParseEnum(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ChatMessageType.CHAT;
        }
        try {
            return ChatMessageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatMessageType.CHAT;
        }
    }

    private ChatMessageResponseDTO convertToDTO(ChatMessage msg) {
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(msg.getMessageId());
        dto.setSender(msg.getSender().name());
        dto.setMessageType(msg.getMessageType().name());
        dto.setText(msg.getMessageText());
        dto.setContextData(msg.getContextData());
        dto.setIsRead(msg.getIsRead());
        dto.setReadAt(msg.getReadAt());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }
}