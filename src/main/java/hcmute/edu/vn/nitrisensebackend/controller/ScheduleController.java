package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.entity.Schedule;
import hcmute.edu.vn.nitrisensebackend.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules") // Bắt buộc phải có dòng này để khớp với đường dẫn bên Android
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // 1. API Lấy danh sách lịch trình theo ngày (Khớp với @GET)
    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedulesByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Schedule> schedules = scheduleRepository.findByUserIdAndScheduleDateAndIsDeletedFalseOrderByStartTimeAsc(userId, date);
        return ResponseEntity.ok(schedules);
    }

    // 2. API Tạo sự kiện mới (Khớp với @POST - Sửa lỗi 404 khi Lưu)
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        // Đảm bảo các giá trị mặc định không bị null
        if (schedule.getCompleted() == null) {
            schedule.setCompleted(false);
        }
        schedule.setDeleted(false);
        schedule.setUserModified(false);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return ResponseEntity.ok(savedSchedule);
    }

    // 3. API Đánh dấu hoàn thành (Khớp với @PATCH - Sửa lỗi 404 khi Tick Checkbox)
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Schedule> toggleScheduleCompletion(
            @PathVariable("id") Long id,
            @RequestParam("isCompleted") boolean isCompleted) {

        return scheduleRepository.findById(id).map(schedule -> {
            schedule.setCompleted(isCompleted);
            Schedule updatedSchedule = scheduleRepository.save(schedule);
            return ResponseEntity.ok(updatedSchedule);
        }).orElse(ResponseEntity.notFound().build());
    }
}