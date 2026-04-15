package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "food_items")
public class FoodItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id") private Long foodId;
    @Column(nullable = false) private String name;
    @Column(length = 100) private String brand;
    @Column(length = 50, unique = true) private String barcode;
    @Column(length = 50) private String category;
    @Column(name = "serving_size", nullable = false, precision = 8, scale = 2) private BigDecimal servingSize;
    @Column(name = "serving_unit", nullable = false, length = 20) private String servingUnit;
    @Column(name = "servings_per_package", precision = 8, scale = 2) private BigDecimal servingsPerPackage;
    @Column(name = "nutrient_basis") private String nutrientBasis;
    @Column(nullable = false, precision = 8, scale = 2) private BigDecimal calories;
    @Column(name = "calories_per_basis", insertable = false, updatable = false) private BigDecimal caloriesPerBasis;
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
    @Column(name = "is_verified") private Boolean isVerified = false;
    @Column(nullable = false) private String source;
    @Column(name = "created_by") private Long createdBy;
    @Column(name = "usage_count") private Integer usageCount = 0;
    @Column(name = "last_used_at") private LocalDateTime lastUsedAt;
    @Column(name = "is_deleted") private Boolean isDeleted = false;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime updatedAt;

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getServingSize() {
        return servingSize;
    }

    public void setServingSize(BigDecimal servingSize) {
        this.servingSize = servingSize;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public BigDecimal getServingsPerPackage() {
        return servingsPerPackage;
    }

    public void setServingsPerPackage(BigDecimal servingsPerPackage) {
        this.servingsPerPackage = servingsPerPackage;
    }

    public String getNutrientBasis() {
        return nutrientBasis;
    }

    public void setNutrientBasis(String nutrientBasis) {
        this.nutrientBasis = nutrientBasis;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public BigDecimal getCaloriesPerBasis() {
        return caloriesPerBasis;
    }

    public void setCaloriesPerBasis(BigDecimal caloriesPerBasis) {
        this.caloriesPerBasis = caloriesPerBasis;
    }

    public BigDecimal getProteinG() {
        return proteinG;
    }

    public void setProteinG(BigDecimal proteinG) {
        this.proteinG = proteinG;
    }

    public BigDecimal getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(BigDecimal carbsG) {
        this.carbsG = carbsG;
    }

    public BigDecimal getFatG() {
        return fatG;
    }

    public void setFatG(BigDecimal fatG) {
        this.fatG = fatG;
    }

    public BigDecimal getFiberG() {
        return fiberG;
    }

    public void setFiberG(BigDecimal fiberG) {
        this.fiberG = fiberG;
    }

    public BigDecimal getVitaminAMcg() {
        return vitaminAMcg;
    }

    public void setVitaminAMcg(BigDecimal vitaminAMcg) {
        this.vitaminAMcg = vitaminAMcg;
    }

    public BigDecimal getVitaminB12Mcg() {
        return vitaminB12Mcg;
    }

    public void setVitaminB12Mcg(BigDecimal vitaminB12Mcg) {
        this.vitaminB12Mcg = vitaminB12Mcg;
    }

    public BigDecimal getVitaminCMg() {
        return vitaminCMg;
    }

    public void setVitaminCMg(BigDecimal vitaminCMg) {
        this.vitaminCMg = vitaminCMg;
    }

    public BigDecimal getVitaminDMcg() {
        return vitaminDMcg;
    }

    public void setVitaminDMcg(BigDecimal vitaminDMcg) {
        this.vitaminDMcg = vitaminDMcg;
    }

    public BigDecimal getIronMg() {
        return ironMg;
    }

    public void setIronMg(BigDecimal ironMg) {
        this.ironMg = ironMg;
    }

    public BigDecimal getCalciumMg() {
        return calciumMg;
    }

    public void setCalciumMg(BigDecimal calciumMg) {
        this.calciumMg = calciumMg;
    }

    public BigDecimal getPotassiumMg() {
        return potassiumMg;
    }

    public void setPotassiumMg(BigDecimal potassiumMg) {
        this.potassiumMg = potassiumMg;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
