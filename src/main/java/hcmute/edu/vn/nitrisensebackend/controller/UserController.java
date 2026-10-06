package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.dto.UserProfileRequest;
import hcmute.edu.vn.nitrisensebackend.entity.User;
import hcmute.edu.vn.nitrisensebackend.repository.UserRepository;
import hcmute.edu.vn.nitrisensebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<User> updateUserProfile(@PathVariable Long userId, @RequestBody User request) {
        // Giao toàn bộ việc cập nhật và tính toán cho Service
        User updatedUser = userService.updateUserProfile(userId, request);

        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@RequestBody Map<String, String> request) {
        String authUid = request.get("authUid");
        String email = request.get("email");

        if (authUid == null || email == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> existingUser = userRepository.findByAuthUid(authUid);

        if (existingUser.isPresent()) {
            return ResponseEntity.ok(existingUser.get());
        } else {
            User newUser = new User();
            newUser.setAuthUid(authUid);
            newUser.setEmail(email);
            newUser.setDeleted(false);

            User savedUser = userRepository.save(newUser);
            return ResponseEntity.ok(savedUser);
        }
    }
}