package hcmute.edu.vn.nitrisensebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.nitrisensebackend.dto.ChatMessageResponseDTO;
import hcmute.edu.vn.nitrisensebackend.entity.ChatMessage;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.entity.User; // Entity bảng users
import hcmute.edu.vn.nitrisensebackend.enums.ChatMessageType;
import hcmute.edu.vn.nitrisensebackend.enums.ChatSender;
import hcmute.edu.vn.nitrisensebackend.repository.ChatMessageRepository;
import hcmute.edu.vn.nitrisensebackend.repository.UserRepository; // Repo bảng users
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final GeminiService geminiService;
    private final DailySummaryService dailySummaryService;
    private final ObjectMapper mapper;
    private final UserRepository userRepository; // THÊM REPO NÀY

    public ChatService(ChatMessageRepository chatRepository,
                       GeminiService geminiService,
                       DailySummaryService dailySummaryService,
                       ObjectMapper mapper,
                       UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.geminiService = geminiService;
        this.dailySummaryService = dailySummaryService;
        this.mapper = mapper;
        this.userRepository = userRepository;
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

        // --------------------------------------------------------------------
        // BƯỚC 1: BUILD RAG CONTEXT (LÀM TRƯỚC KHI LƯU DB ĐỂ TRÁNH LẶP TIN NHẮN)
        // --------------------------------------------------------------------
        StringBuilder contextBuilder = new StringBuilder("Ngữ cảnh hiện tại của User:\n");

        // 1.1 Lấy User Profile (Check null cẩn thận)
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                if (user.getWeightKg() != null && user.getHeightCm() != null) {
                    contextBuilder.append(String.format("- Chỉ số cơ thể: Cân nặng %.1f kg, Chiều cao %.1f cm.\n",
                            user.getWeightKg(), user.getHeightCm()));
                }
                if (user.getDailyCalorieGoal() != null && user.getWaterGoalMl() != null) {
                    contextBuilder.append(String.format("- Mục tiêu mỗi ngày: %d kcal, %d ml nước.\n",
                            user.getDailyCalorieGoal(), user.getWaterGoalMl()));
                }
            }
        } catch (Exception e) {
            System.err.println("[ChatService] Lỗi lấy User Info: " + e.getMessage());
        }

        // 1.2 Lấy Dinh dưỡng hôm nay
        try {
            DailySummary summary = dailySummaryService.getSummaryByDate(userId, LocalDate.now());
            if (summary != null) {
                contextBuilder.append(String.format("- Dinh dưỡng hôm nay đã nạp: Calo: %d, Pro: %s, Carbs: %s, Fat: %s, Nước: %d ml\n",
                        summary.getTotalCalories(), summary.getTotalProteinG(), summary.getTotalCarbsG(), summary.getTotalFatG(), summary.getTotalWaterMl()));
            }
        } catch (Exception ignored) {}

        // 1.3 Lấy Lịch sử Chat gần đây (Lúc này CHƯA có tin nhắn hiện tại)
        try {
            List<ChatMessage> recentChats = chatRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
            Collections.reverse(recentChats);
            contextBuilder.append("- Lịch sử chat gần đây:\n");
            for (ChatMessage msg : recentChats) {
                contextBuilder.append("  + ").append(msg.getSender().name()).append(": ").append(msg.getMessageText()).append("\n");
            }
        } catch (Exception ignored) {}

        // --------------------------------------------------------------------
        // BƯỚC 2: BÂY GIỜ MỚI LƯU TIN NHẮN USER XUỐNG DB
        // --------------------------------------------------------------------
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setSender(ChatSender.USER);
        userMsg.setMessageType(ChatMessageType.CHAT);
        userMsg.setMessageText(userText);
        userMsg.setIsRead(true);
        chatRepository.save(userMsg);

        // --------------------------------------------------------------------
        // BƯỚC 3: PROMPT ENGINEERING NÂNG CẤP (Chống bịa chuyện - Hallucination)
        // --------------------------------------------------------------------
        String systemPrompt = contextBuilder.toString() + "\n\n" +
                "Bạn là trợ lý dinh dưỡng NitriSense.\n" +
                "BẮT BUỘC:\n" +
                "- Sử dụng số liệu trong 'Ngữ cảnh hiện tại' để tính toán và tư vấn.\n" +
                "- TUYỆT ĐỐI KHÔNG nói 'không có quyền truy cập'. Nếu dữ liệu bị thiếu, hãy giải thích từ tốn là bạn chưa có thông tin đó và hướng dẫn người dùng cập nhật, KHÔNG được tự bịa số liệu.\n" +
                "- Phân loại message_type: 'WARNING' (thiếu/dư chất/nước nghiêm trọng), 'ACHIEVEMENT' (đạt mục tiêu), 'CHAT' (bình thường).\n" +
                "Lưu ý: Trả lời ngắn gọn, thân thiện. Nếu có cảnh báo số liệu quan trọng, đưa nó vào trường context_data ở dạng JSON.";

        // --------------------------------------------------------------------
        // BƯỚC 4: GỌI GEMINI & LƯU PHẢN HỒI
        // --------------------------------------------------------------------
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setSender(ChatSender.AI);
        aiMsg.setIsRead(false);

        try {
            String aiJson = geminiService.generateChatResponse(systemPrompt, userText);
            JsonNode root = mapper.readTree(aiJson);

            aiMsg.setMessageText(root.path("response_text").asText());
            aiMsg.setMessageType(safeParseEnum(root.path("message_type").asText()));

            // Xử lý contextData (Giữ nguyên cấu trúc JsonNode)
            if (root.has("context_data") && !root.get("context_data").isNull() && !root.get("context_data").isEmpty()) {
                aiMsg.setContextData(root.get("context_data"));
            }

        } catch (Exception e) {
            System.err.println("[ChatService] Lỗi gọi AI hoặc Parse JSON: " + e.getMessage());
            aiMsg.setMessageType(ChatMessageType.WARNING);
            aiMsg.setMessageText("Hệ thống AI đang bận hoặc quá tải. Vui lòng thử lại sau ít phút nhé!");
        }

        return convertToDTO(chatRepository.save(aiMsg));
    }

    // HÀM AN TOÀN CHỐNG CRASH KHI PARSE ENUM
    private ChatMessageType safeParseEnum(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ChatMessageType.CHAT;
        }
        try {
            return ChatMessageType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("[ChatService] Cảnh báo: AI trả về MessageType lạ: " + value);
            return ChatMessageType.CHAT; // Rơi về mặc định nếu Gemini trả bậy
        }
    }

    // CHUYỂN ENTITY SANG DTO (BẢO TOÀN JSON NODE)
    private ChatMessageResponseDTO convertToDTO(ChatMessage msg) {
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(msg.getMessageId());
        dto.setSender(msg.getSender().name());
        dto.setMessageType(msg.getMessageType().name());
        dto.setText(msg.getMessageText());

        // Truyền thẳng JsonNode, Jackson sẽ tự lo việc hiển thị JSON đẹp ra Client
        dto.setContextData(msg.getContextData());

        dto.setIsRead(msg.getIsRead());
        dto.setReadAt(msg.getReadAt());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }
}