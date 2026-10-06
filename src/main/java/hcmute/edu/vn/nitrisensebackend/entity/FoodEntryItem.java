package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_entry_items")
public class FoodEntryItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id") private Long itemId;

    @Column(name = "entry_id", nullable = false) private Long entryId;

    @Column(name = "food_id") private Long foodId;

    @Column(name = "custom_name") private String customName;

    @Column(name = "serving_qty", nullable = false, precision = 8, scale = 2) private BigDecimal servingQty;

    @Column(name = "serving_unit", length = 20) private String servingUnit;

    @Column(name = "calories_per_serving", nullable = false, precision = 8, scale = 2) private BigDecimal caloriesPerServing;

    // CẤU HÌNH QUAN TRỌNG: Báo cho Hibernate biết cột này do MySQL tự sinh ra (GENERATED ALWAYS)
    @Column(name = "calories", insertable = false, updatable = false)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 8, scale = 2) private BigDecimal proteinG;
    @Column(name = "carbs_g", precision = 8, scale = 2) private BigDecimal carbsG;
    @Column(name = "fat_g", precision = 8, scale = 2) private BigDecimal fatG;
    @Column(name = "fiber_g", precision = 8, scale = 2) private BigDecimal fiberG;
    @Column(name = "vitamin_a_mcg", precision = 8, scale = 2) private BigDecimal vitaminAMcg;
    @Column(name = "vitamin_b12_mcg", precision = 8, scale = 2) private BigDecimal vitaminB12Mcg;
    @Column(name = "vitamin_c_mg", precision = 8, scale = 2) private BigDecimal vitaminCMg;
    @Column(name = "vitamin_d_mcg", precision = 8, scale = 2) private BigDecimal vitaminDMcg;
    @Column(name = "iron_mg", precision = 8, scale = 2) private BigDecimal ironMg;
    @Column(name = "calcium_mg", precision = 8, scale = 2) private BigDecimal calciumMg;
    @Column(name = "potassium_mg", precision = 8, scale = 2) private BigDecimal potassiumMg;

    @Column(nullable = false) private String source;

    @Column(name = "raw_input", columnDefinition = "TEXT") private String rawInput;

    @Column(name = "image_urls", columnDefinition = "JSON") private String imageUrls;

    @Column(name = "is_deleted") private Boolean isDeleted = false;

    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }

    public String getCustomName() { return customName; }
    public void setCustomName(String customName) { this.customName = customName; }

    public BigDecimal getServingQty() { return servingQty; }
    public void setServingQty(BigDecimal servingQty) { this.servingQty = servingQty; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public BigDecimal getCaloriesPerServing() { return caloriesPerServing; }
    public void setCaloriesPerServing(BigDecimal caloriesPerServing) { this.caloriesPerServing = caloriesPerServing; }

    public BigDecimal getCalories() { return calories; }
    public void setCalories(BigDecimal calories) { this.calories = calories; }

    public BigDecimal getProteinG() { return proteinG; }
    public void setProteinG(BigDecimal proteinG) { this.proteinG = proteinG; }

    public BigDecimal getCarbsG() { return carbsG; }
    public void setCarbsG(BigDecimal carbsG) { this.carbsG = carbsG; }

    public BigDecimal getFatG() { return fatG; }
    public void setFatG(BigDecimal fatG) { this.fatG = fatG; }

    public BigDecimal getFiberG() { return fiberG; }
    public void setFiberG(BigDecimal fiberG) { this.fiberG = fiberG; }

    public BigDecimal getVitaminAMcg() { return vitaminAMcg; }
    public void setVitaminAMcg(BigDecimal vitaminAMcg) { this.vitaminAMcg = vitaminAMcg; }

    public BigDecimal getVitaminB12Mcg() { return vitaminB12Mcg; }
    public void setVitaminB12Mcg(BigDecimal vitaminB12Mcg) { this.vitaminB12Mcg = vitaminB12Mcg; }

    public BigDecimal getVitaminCMg() { return vitaminCMg; }
    public void setVitaminCMg(BigDecimal vitaminCMg) { this.vitaminCMg = vitaminCMg; }

    public BigDecimal getVitaminDMcg() { return vitaminDMcg; }
    public void setVitaminDMcg(BigDecimal vitaminDMcg) { this.vitaminDMcg = vitaminDMcg; }

    public BigDecimal getIronMg() { return ironMg; }
    public void setIronMg(BigDecimal ironMg) { this.ironMg = ironMg; }

    public BigDecimal getCalciumMg() { return calciumMg; }
    public void setCalciumMg(BigDecimal calciumMg) { this.calciumMg = calciumMg; }

    public BigDecimal getPotassiumMg() { return potassiumMg; }
    public void setPotassiumMg(BigDecimal potassiumMg) { this.potassiumMg = potassiumMg; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getRawInput() { return rawInput; }
    public void setRawInput(String rawInput) { this.rawInput = rawInput; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public Boolean getDeleted() { return isDeleted; }
    public void setDeleted(Boolean deleted) { isDeleted = deleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}