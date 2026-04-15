package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.repository.DailySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    /**
     * API GET: Lấy tổng kết dinh dưỡng ngày hôm nay để hiển thị lên màn hình chính của App Android
     * Đường dẫn: http://localhost:8080/api/home/summary?userId=1
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getDailySummary(@RequestParam Long userId) {
        LocalDate today = LocalDate.now();

        // Tìm dòng tổng kết của ngày hôm nay trong bảng daily_summaries
        Optional<DailySummary> summary = dailySummaryRepository.findByUserIdAndSummaryDate(userId, today);

        if (summary.isPresent()) {
            return ResponseEntity.ok(summary.get());
        } else {
            // Nếu người dùng chưa ăn gì hôm nay, trả về một đối tượng rỗng với các chỉ số bằng 0
            DailySummary emptySummary = new DailySummary();
            emptySummary.setUserId(userId);
            emptySummary.setSummaryDate(today);
            return ResponseEntity.ok(emptySummary);
        }
    }
}