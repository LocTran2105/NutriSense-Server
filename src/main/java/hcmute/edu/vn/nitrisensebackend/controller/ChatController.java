package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.dto.ChatMessageResponseDTO;
import hcmute.edu.vn.nitrisensebackend.dto.ChatSendRequest;
import hcmute.edu.vn.nitrisensebackend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageResponseDTO>> getHistory(@RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getChatHistory(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(Map.of("unread_count", chatService.getUnreadBadgeCount(userId)));
    }

    @PutMapping("/mark-read")
    public ResponseEntity<?> markAsRead(@RequestParam Long userId) {
        chatService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody ChatSendRequest request) {
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin định danh người dùng."));
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tin nhắn không được để trống."));
        }

        try {
            return ResponseEntity.ok(chatService.processUserMessage(request.getUserId(), request.getMessage()));
        } catch (Exception e) {
            System.err.println("[ChatController] User: " + request.getUserId() + " | Exception: " + e.getMessage());

            return ResponseEntity.internalServerError().body(Map.of("error", "Hệ thống AI đang bận hoặc gặp sự cố. Vui lòng thử lại sau."));
        }
    }
}