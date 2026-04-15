package hcmute.edu.vn.nitrisensebackend.controller;

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
     * Luồng này KHÔNG lưu vào nhật ký món ăn ngay lập tức để người dùng có thể kiểm tra lại.
     */
    @PostMapping("/analyze-image")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("userId") Long userId,
            @RequestParam("image") MultipartFile file) {
        try {
            // Kiểm tra tính hợp lệ của file đầu vào
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File ảnh không được để trống"));
            }

            // Gọi service xử lý đa phương thức (Multimodal) qua Gemini API
            List<NutrientDto> result = aiService.analyzeFoodImage(userId, file);

            // Trả về danh sách các món ăn nhận diện được dưới dạng JSON Array cho Client
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            // Xử lý các lỗi nghiệp vụ từ AiService (ví dụ: lỗi parse JSON, lỗi kết nối API)
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi phân tích: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        } catch (Exception e) {
            // Xử lý các lỗi hệ thống không mong muốn khác
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi hệ thống nghiêm trọng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
        }
    }
}