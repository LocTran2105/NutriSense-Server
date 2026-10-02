package hcmute.edu.vn.nitrisensebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/water")
public class WaterController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/add")
    public ResponseEntity<?> addWater(@RequestParam Long userId, @RequestParam int amountMl) {
        LocalDate today = LocalDate.now();

        try {
            // 1. Thêm vào water_intake
            String insertSql = "INSERT INTO water_intake (user_id, intake_date, amount_ml, source) VALUES (?, ?, ?, 'manual')";
            jdbcTemplate.update(insertSql, userId, today, amountMl);

            // 2. Tính tổng nước hôm nay từ water_intake
            Integer totalWater = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount_ml), 0) FROM water_intake WHERE user_id = ? AND intake_date = ?",
                Integer.class, userId, today
            );

            // 3. Cập nhật daily_summaries (upsert)
            String upsertSql = """
                INSERT INTO daily_summaries (user_id, summary_date, total_water_ml, total_calories, total_protein_g, total_carbs_g, total_fat_g, num_meals, created_at, updated_at)
                VALUES (?, ?, ?, 0, 0, 0, 0, 0, NOW(), NOW())
                ON DUPLICATE KEY UPDATE
                    total_water_ml = ?,
                    updated_at = NOW()
                """;
            jdbcTemplate.update(upsertSql, userId, today, totalWater, totalWater);

            return ResponseEntity.ok("Đã thêm " + amountMl + "ml nước! Tổng hôm nay: " + totalWater + "ml");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}