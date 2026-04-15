package hcmute.edu.vn.nitrisensebackend.dto;

public class UserProfileRequest {
    private Integer age; // Tuổi (Để backend tự tính ra năm sinh)
    private Double heightCm;
    private Double weightKg;
    private String gender; // 'male', 'female'
    private String activityLevel; // 'sedentary', 'light', 'moderate', 'active', 'very_active'
    private Integer dailyCalorieGoal;

    public Integer getWaterGoalMl() {
        return waterGoalMl;
    }

    public void setWaterGoalMl(Integer waterGoalMl) {
        this.waterGoalMl = waterGoalMl;
    }

    public Integer getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(Integer dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private Integer waterGoalMl;
}