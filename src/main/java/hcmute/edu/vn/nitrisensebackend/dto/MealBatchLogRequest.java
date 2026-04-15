package hcmute.edu.vn.nitrisensebackend.dto;

import java.time.LocalDate;
import java.util.List;

public class MealBatchLogRequest {
    private Long userId;
    private String mealType;
    private LocalDate date;
    private List<NutrientDto> items;
    private String imageUrls;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<NutrientDto> getItems() { return items; }
    public void setItems(List<NutrientDto> items) { this.items = items; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }
}