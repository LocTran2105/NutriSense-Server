package hcmute.edu.vn.nitrisensebackend.service;

import hcmute.edu.vn.nitrisensebackend.entity.User;
import hcmute.edu.vn.nitrisensebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updateUserProfile(Long userId, User request) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 1. Cập nhật dữ liệu thô
            if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
            if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
            if (request.getHeightCm() != null) user.setHeightCm(request.getHeightCm());
            if (request.getWeightKg() != null) user.setWeightKg(request.getWeightKg());
            if (request.getGender() != null) user.setGender(request.getGender());
            if (request.getActivityLevel() != null) user.setActivityLevel(request.getActivityLevel());
            if (request.getGoal() != null) user.setGoal(request.getGoal());

            // 2. Gọi hàm tính toán các chỉ số sức khỏe
            calculateHealthMetrics(user);

            // 3. Lưu xuống Database
            return userRepository.save(user);
        }
        return null; // Trả về null nếu không tìm thấy user (hoặc ném Exception tùy chuẩn dự án của bạn)
    }

    private void calculateHealthMetrics(User user) {
        // Chỉ tính toán khi có đủ 4 trường dữ liệu cơ bản
        if (user.getWeightKg() != null && user.getHeightCm() != null && user.getDateOfBirth() != null && user.getGender() != null) {

            // Tính tuổi
            int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();

            // Tính BMR (Mifflin-St Jeor)
            double bmr = (10 * user.getWeightKg().doubleValue())
                    + (6.25 * user.getHeightCm().doubleValue())
                    - (5 * age);
            bmr += user.getGender().equalsIgnoreCase("male") ? 5 : -161;

            // Hệ số TDEE
            double tdeeMultiplier = 1.2;
            String activity = user.getActivityLevel() != null ? user.getActivityLevel().toLowerCase() : "";
            switch (activity) {
                case "light": case "lightly_active": tdeeMultiplier = 1.375; break;
                case "moderate": case "moderately_active": tdeeMultiplier = 1.55; break;
                case "active": case "very_active": tdeeMultiplier = 1.725; break;
                case "extra_active": tdeeMultiplier = 1.9; break;
            }

            double tdee = bmr * tdeeMultiplier;

            // Tính Calo mục tiêu
            double calorieGoal = tdee;
            String goal = user.getGoal() != null ? user.getGoal().toLowerCase() : "";
            switch (goal) {
                case "lose_weight": calorieGoal -= 500; break;
                case "gain_weight": case "build_muscle": calorieGoal += 500; break;
            }

            double minCalorie = user.getGender().equalsIgnoreCase("male") ? 1500 : 1200;
            user.setDailyCalorieGoal(Math.max((int) Math.round(calorieGoal), (int) minCalorie));

            // Tính Nước mục tiêu
            double waterMl = user.getWeightKg().doubleValue() * 35;
            if (tdeeMultiplier >= 1.55) {
                waterMl += 500;
            }
            user.setWaterGoalMl((int) Math.round(waterMl));
        }
    }
}