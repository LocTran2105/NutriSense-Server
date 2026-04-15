package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.UserGoal;
import hcmute.edu.vn.nitrisensebackend.repository.UserGoalRepository;
import hcmute.edu.vn.nitrisensebackend.service.AiService;
import hcmute.edu.vn.nitrisensebackend.service.DailySummaryService;
import hcmute.edu.vn.nitrisensebackend.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiReminderController {

    @Autowired
    private AiService aiService;

    @Autowired
    private MealService mealService;

    @Autowired
    private DailySummaryService dailySummaryService;

    // TIÊM UserGoalRepository ĐỂ LẤY MỤC TIÊU THẬT
    @Autowired
    private UserGoalRepository userGoalRepository;

    @GetMapping("/daily-reminder")
    public ResponseEntity<Map<String, String>> getDailyReminder(@RequestParam Long userId) {

        // 1. Lấy dữ liệu tổng kết ngày hiện tại
        DailySummary summary = dailySummaryService.getSummaryByDate(userId, LocalDate.now());

        // 2. Lấy danh sách các món ăn
        List<FoodEntryItem> todayItems = mealService.getMealItemsForWholeDay(userId, LocalDate.now());

        // 3. LẤY MỤC TIÊU THẬT TỪ BẢNG user_goals
        List<UserGoal> userGoals = userGoalRepository.findByUserIdAndIsDeletedFalse(userId);

        // 4. Nhờ AI tính toán và viết thông báo (Truyền userGoals vào)
        String message = aiService.generateDailyReminder(userId, userGoals, todayItems, summary);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}