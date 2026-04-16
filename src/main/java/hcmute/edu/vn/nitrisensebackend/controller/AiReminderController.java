package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.dto.ReminderResponseDto;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.User;
import hcmute.edu.vn.nitrisensebackend.entity.UserGoal;
import hcmute.edu.vn.nitrisensebackend.repository.UserGoalRepository;
import hcmute.edu.vn.nitrisensebackend.repository.UserRepository;
import hcmute.edu.vn.nitrisensebackend.service.AiService;
import hcmute.edu.vn.nitrisensebackend.service.DailySummaryService;
import hcmute.edu.vn.nitrisensebackend.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiReminderController {

    @Autowired private AiService aiService;
    @Autowired private MealService mealService;
    @Autowired private DailySummaryService dailySummaryService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/daily-reminder")
    public ResponseEntity<ReminderResponseDto> getDailyReminder(@RequestParam Long userId) {
        DailySummary summary = dailySummaryService.getSummaryByDate(userId, LocalDate.now());
        List<FoodEntryItem> todayItems = mealService.getMealItemsForWholeDay(userId, LocalDate.now());

        // LẤY MỤC TIÊU TỪ USER PROFILE
        User profile = userRepository.findById(userId).orElse(null);

        // Đổi logic trong AiService để nhận UserProfile thay vì List<UserGoal>
        ReminderResponseDto response = aiService.generateDailyReminder(userId, profile, todayItems, summary);

        return ResponseEntity.ok(response);
    }
}