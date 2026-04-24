package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_summaries")
public class DailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long summaryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(name = "total_calories")
    private Integer totalCalories = 0;

    @Column(name = "total_protein_g", precision = 8, scale = 2)
    private BigDecimal totalProteinG;

    @Column(name = "total_carbs_g", precision = 8, scale = 2)
    private BigDecimal totalCarbsG;

    @Column(name = "total_fat_g", precision = 8, scale = 2)
    private BigDecimal totalFatG;

    @Column(name = "total_water_ml")
    private Integer totalWaterMl = 0;

    @Column(name = "sleep_hours", precision = 4, scale = 2)
    private BigDecimal sleepHours;

    @Column(name = "num_meals")
    private Integer numMeals = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public DailySummary() {}

    public DailySummary(Long userId, LocalDate summaryDate) {
        this.userId = userId;
        this.summaryDate = summaryDate;
    }


    public Long getSummaryId() { return summaryId; }
    public void setSummaryId(Long summaryId) { this.summaryId = summaryId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getSummaryDate() { return summaryDate; }
    public void setSummaryDate(LocalDate summaryDate) { this.summaryDate = summaryDate; }

    public Integer getTotalCalories() { return totalCalories; }
    public void setTotalCalories(Integer totalCalories) { this.totalCalories = totalCalories; }

    public BigDecimal getTotalProteinG() { return totalProteinG; }
    public void setTotalProteinG(BigDecimal totalProteinG) { this.totalProteinG = totalProteinG; }

    public BigDecimal getTotalCarbsG() { return totalCarbsG; }
    public void setTotalCarbsG(BigDecimal totalCarbsG) { this.totalCarbsG = totalCarbsG; }

    public BigDecimal getTotalFatG() { return totalFatG; }
    public void setTotalFatG(BigDecimal totalFatG) { this.totalFatG = totalFatG; }

    public Integer getTotalWaterMl() { return totalWaterMl; }
    public void setTotalWaterMl(Integer totalWaterMl) { this.totalWaterMl = totalWaterMl; }

    public BigDecimal getSleepHours() { return sleepHours; }
    public void setSleepHours(BigDecimal sleepHours) { this.sleepHours = sleepHours; }

    public Integer getNumMeals() { return numMeals; }
    public void setNumMeals(Integer numMeals) { this.numMeals = numMeals; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}