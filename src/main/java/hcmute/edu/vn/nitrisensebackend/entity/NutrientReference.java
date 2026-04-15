package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "nutrient_reference")
public class NutrientReference {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ref_id") private Long refId;
    @Column(nullable = false) private String gender;
    @Column(name = "activity_level", nullable = false) private String activityLevel;
    @Column(name = "age_min", nullable = false) private Integer ageMin;
    @Column(name = "age_max", nullable = false) private Integer ageMax;
    @Column(name = "pregnancy") private Boolean pregnancy = false;
    @Column(name = "calories_kcal") private Integer caloriesKcal;
    @Column(name = "protein_g", precision = 5, scale = 2) private BigDecimal proteinG;
    @Column(name = "vitamin_a_mcg") private Integer vitaminAMcg;
    @Column(name = "vitamin_b12_mcg", precision = 4, scale = 2) private BigDecimal vitaminB12Mcg;
    @Column(name = "vitamin_c_mg") private Integer vitaminCMg;
    @Column(name = "vitamin_d_mcg", precision = 4, scale = 2) private BigDecimal vitaminDMcg;
    @Column(name = "iron_mg", precision = 5, scale = 2) private BigDecimal ironMg;
    @Column(name = "calcium_mg") private Integer calciumMg;
    @Column(name = "potassium_mg") private Integer potassiumMg;
    @Column(name = "water_ml") private Integer waterMl;

    public BigDecimal getProteinG() {
        return proteinG;
    }

    public void setProteinG(BigDecimal proteinG) {
        this.proteinG = proteinG;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    public Boolean getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(Boolean pregnancy) {
        this.pregnancy = pregnancy;
    }

    public Integer getCaloriesKcal() {
        return caloriesKcal;
    }

    public void setCaloriesKcal(Integer caloriesKcal) {
        this.caloriesKcal = caloriesKcal;
    }

    public Integer getVitaminAMcg() {
        return vitaminAMcg;
    }

    public void setVitaminAMcg(Integer vitaminAMcg) {
        this.vitaminAMcg = vitaminAMcg;
    }

    public BigDecimal getVitaminB12Mcg() {
        return vitaminB12Mcg;
    }

    public void setVitaminB12Mcg(BigDecimal vitaminB12Mcg) {
        this.vitaminB12Mcg = vitaminB12Mcg;
    }

    public Integer getVitaminCMg() {
        return vitaminCMg;
    }

    public void setVitaminCMg(Integer vitaminCMg) {
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

    public Integer getCalciumMg() {
        return calciumMg;
    }

    public void setCalciumMg(Integer calciumMg) {
        this.calciumMg = calciumMg;
    }

    public Integer getPotassiumMg() {
        return potassiumMg;
    }

    public void setPotassiumMg(Integer potassiumMg) {
        this.potassiumMg = potassiumMg;
    }

    public Integer getWaterMl() {
        return waterMl;
    }

    public void setWaterMl(Integer waterMl) {
        this.waterMl = waterMl;
    }
}
