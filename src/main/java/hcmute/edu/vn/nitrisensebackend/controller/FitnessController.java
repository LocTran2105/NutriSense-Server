package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.entity.ExerciseTest;
import hcmute.edu.vn.nitrisensebackend.repository.ExerciseTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fitness")
public class FitnessController {

    @Autowired
    private ExerciseTestRepository fitnessRepo;

    @PostMapping("/log")
    public ResponseEntity<ExerciseTest> logFitnessTest(@RequestBody ExerciseTest request) {
        request.setTestDate(LocalDate.now());
        ExerciseTest saved = fitnessRepo.save(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ExerciseTest>> getRecentTests(@RequestParam Long userId) {
        return ResponseEntity.ok(fitnessRepo.findTop10ByUserIdOrderByTestDateDescTestIdDesc(userId));
    }
}