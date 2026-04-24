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