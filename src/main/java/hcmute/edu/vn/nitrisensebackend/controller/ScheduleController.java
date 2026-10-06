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
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;


    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedulesByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Schedule> schedules = scheduleRepository.findByUserIdAndScheduleDateAndIsDeletedFalseOrderByStartTimeAsc(userId, date);
        return ResponseEntity.ok(schedules);
    }


    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        if (schedule.getCompleted() == null) {
            schedule.setCompleted(false);
        }
        schedule.setDeleted(false);
        schedule.setUserModified(false);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return ResponseEntity.ok(savedSchedule);
    }

    // API Cập nhật (Sửa) lịch trình
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable("id") Long id,
            @RequestBody Schedule updatedSchedule) {

        return scheduleRepository.findById(id).map(existingSchedule -> {
            existingSchedule.setTitle(updatedSchedule.getTitle());
            existingSchedule.setNotes(updatedSchedule.getNotes());
            existingSchedule.setStartTime(updatedSchedule.getStartTime());
            // is_user_modified được set = true khi người dùng chỉnh sửa
            existingSchedule.setUserModified(true);

            Schedule savedSchedule = scheduleRepository.save(existingSchedule);
            return ResponseEntity.ok(savedSchedule);
        }).orElse(ResponseEntity.notFound().build());
    }

    // API Xóa lịch trình
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable("id") Long id) {
        return scheduleRepository.findById(id).map(schedule -> {
            // Xóa mềm hoặc xóa cứng. Dựa theo cấu trúc database có cờ is_deleted, ta dùng xóa mềm:
            schedule.setDeleted(true);
            scheduleRepository.save(schedule);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

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