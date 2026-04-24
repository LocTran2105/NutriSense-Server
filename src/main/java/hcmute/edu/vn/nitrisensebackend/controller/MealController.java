package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.dto.AiAnalyzeResult;
import hcmute.edu.vn.nitrisensebackend.dto.MealLogRequest;
import hcmute.edu.vn.nitrisensebackend.dto.MealBatchLogRequest;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.FoodItem;
import hcmute.edu.vn.nitrisensebackend.service.AiService;
import hcmute.edu.vn.nitrisensebackend.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired private MealService mealService;
    @Autowired private AiService aiService;

    private final ConcurrentHashMap<String, Long> requestCache = new ConcurrentHashMap<>();

    @PostMapping("/log")
    public ResponseEntity<?> logMealFromAi(@RequestBody MealLogRequest request) {

        String cacheKey = request.getUserId() + "_" + request.getUserInput();
        long now = System.currentTimeMillis();

        if (requestCache.containsKey(cacheKey) && (now - requestCache.get(cacheKey) < 5000)) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Bạn thao tác quá nhanh, vui lòng đợi 5 giây!");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err);
        }
        requestCache.put(cacheKey, now);

        try {
            AiAnalyzeResult aiResult = aiService.analyzeFood(request.getUserId(), request.getUserInput());

            if (aiResult.isAskUser()) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "ask_user");
                response.put("question", aiResult.getQuestion());
                return ResponseEntity.status(422).body(response);
            }

            List<FoodEntryItem> savedItems = new ArrayList<>();

            if (aiResult.getFoodItems() != null) {
                for (FoodItem item : aiResult.getFoodItems()) {
                    FoodEntryItem saved = mealService.saveMealData(
                            request.getUserId(),
                            request.getMealType(),
                            request.getUserInput(),
                            item,
                            "text",
                            null
                    );
                    savedItems.add(saved);
                }
            }

            if (!savedItems.isEmpty()) {
                return ResponseEntity.ok(savedItems.get(0));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Không lưu được món ăn nào"));
            }

        } catch (RestClientException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi kết nối máy chủ AI: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(err);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PostMapping("/log/batch")
    public ResponseEntity<?> logMealBatch(@RequestBody MealBatchLogRequest request) {
        try {
            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Danh sách món ăn trống"));
            }
            mealService.saveMealFromAi(
                    request.getUserId(),
                    request.getMealType(),
                    request.getDate() != null ? request.getDate() : LocalDate.now(),
                    request.getItems(),
                    request.getImageUrls()
            );
            return ResponseEntity.ok(Map.of("message", "Lưu bữa ăn thành công!"));
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Lỗi lưu dữ liệu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/items")
    public ResponseEntity<List<FoodEntryItem>> getMealItems(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String mealType) {
        List<FoodEntryItem> items = mealService.getMealItems(userId, date, mealType);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/entry")
    public ResponseEntity<?> deleteMeal(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String mealType) {
        mealService.deleteWholeMeal(userId, date, mealType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteMealItem(@PathVariable Long itemId) {
        mealService.deleteMealItem(itemId);
        return ResponseEntity.ok().build();
    }
}