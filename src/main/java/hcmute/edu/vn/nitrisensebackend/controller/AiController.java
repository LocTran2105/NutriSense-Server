package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.dto.AiAnalyzeResult;
import hcmute.edu.vn.nitrisensebackend.dto.NutrientDto;
import hcmute.edu.vn.nitrisensebackend.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * API POST: Tiếp nhận hình ảnh món ăn và trả về danh sách dinh dưỡng dự đoán.
     */
    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("userId") Long userId,
            @RequestParam("image") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File ảnh không được để trống"));
            }

            List<NutrientDto> result = aiService.analyzeFoodImage(userId, file);

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi phân tích: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi hệ thống nghiêm trọng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
        }
    }

    @PostMapping("/analyze-text")
    public ResponseEntity<?> analyzeText(
            @RequestParam("userId") Long userId,
            @RequestBody Map<String, String> payload) {
        try {
            String userInput = payload.get("userInput");
            if (userInput == null || userInput.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nội dung nhập không được để trống"));
            }

            AiAnalyzeResult result = aiService.analyzeFood(userId, userInput);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi phân tích: " + e.getMessage()));
        }
    }
}