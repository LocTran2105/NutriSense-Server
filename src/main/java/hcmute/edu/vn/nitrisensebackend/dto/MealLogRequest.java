package hcmute.edu.vn.nitrisensebackend.dto;

public class MealLogRequest {
    private Long userId;
    private String mealType; // "breakfast", "lunch", "dinner", "snack"
    private String userInput; // Ví dụ: "1 bát phở bò"

    // Alt + Insert -> Tạo Getter và Setter cho 3 biến này nhé
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
}