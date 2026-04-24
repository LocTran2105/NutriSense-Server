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
        String sql = "INSERT INTO water_intake (user_id, intake_date, amount_ml, source) VALUES (?, ?, ?, 'manual')";
        LocalDate today = LocalDate.now();

        try {
            jdbcTemplate.update(sql, userId, today, amountMl);
            return ResponseEntity.ok("Đã thêm " + amountMl + "ml nước!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}