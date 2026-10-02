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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    /**
     * API GET: Lấy tổng kết dinh dưỡng theo ngày
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getDailySummary(
            @RequestParam Long userId,
            @RequestParam(required = false) String date) {

        // Dùng date từ client nếu có, không thì dùng ngày hôm nay
        LocalDate targetDate;
        try {
            targetDate = (date != null && !date.isEmpty())
                    ? LocalDate.parse(date)
                    : LocalDate.now();
        } catch (Exception e) {
            targetDate = LocalDate.now();
        }

        Optional<DailySummary> summaryOpt = dailySummaryRepository.findByUserIdAndSummaryDate(userId, targetDate);

        // Trả về Map với field names khớp với Flutter model
        Map<String, Object> result = new LinkedHashMap<>();
        if (summaryOpt.isPresent()) {
            DailySummary s = summaryOpt.get();
            result.put("totalCalories", s.getTotalCalories() != null ? s.getTotalCalories() : 0);
            result.put("totalProteinG", s.getTotalProteinG() != null ? s.getTotalProteinG().doubleValue() : 0.0);
            result.put("totalCarbsG", s.getTotalCarbsG() != null ? s.getTotalCarbsG().doubleValue() : 0.0);
            result.put("totalFatG", s.getTotalFatG() != null ? s.getTotalFatG().doubleValue() : 0.0);
            result.put("totalWaterMl", s.getTotalWaterMl() != null ? s.getTotalWaterMl() : 0);
            result.put("date", targetDate.toString());
        } else {
            result.put("totalCalories", 0);
            result.put("totalProteinG", 0.0);
            result.put("totalCarbsG", 0.0);
            result.put("totalFatG", 0.0);
            result.put("totalWaterMl", 0);
            result.put("date", targetDate.toString());
        }
        return ResponseEntity.ok(result);
    }
}