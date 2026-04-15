package hcmute.edu.vn.nitrisensebackend.dto;

import java.math.BigDecimal;

public class NutrientSumDto {

    private BigDecimal calories;
    private BigDecimal proteinG;
    private BigDecimal carbsG;
    private BigDecimal fatG;
    private BigDecimal fiberG;
    private BigDecimal vitaminAMcg;
    private BigDecimal vitaminB12Mcg;
    private BigDecimal vitaminCMg;
    private BigDecimal vitaminDMcg;
    private BigDecimal ironMg;
    private BigDecimal calciumMg;
    private BigDecimal potassiumMg;

    // Constructor được JPA gọi khi dùng từ khóa "new" trong câu lệnh @Query
    public NutrientSumDto(
            BigDecimal calories, BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatG,
            BigDecimal fiberG, BigDecimal vitaminAMcg, BigDecimal vitaminB12Mcg,
            BigDecimal vitaminCMg, BigDecimal vitaminDMcg, BigDecimal ironMg,
            BigDecimal calciumMg, BigDecimal potassiumMg) {

        // Chặn lỗi NULL an toàn tuyệt đối
        this.calories = calories != null ? calories : BigDecimal.ZERO;
        this.proteinG = proteinG != null ? proteinG : BigDecimal.ZERO;
        this.carbsG = carbsG != null ? carbsG : BigDecimal.ZERO;
        this.fatG = fatG != null ? fatG : BigDecimal.ZERO;
        this.fiberG = fiberG != null ? fiberG : BigDecimal.ZERO;
        this.vitaminAMcg = vitaminAMcg != null ? vitaminAMcg : BigDecimal.ZERO;
        this.vitaminB12Mcg = vitaminB12Mcg != null ? vitaminB12Mcg : BigDecimal.ZERO;
        this.vitaminCMg = vitaminCMg != null ? vitaminCMg : BigDecimal.ZERO;
        this.vitaminDMcg = vitaminDMcg != null ? vitaminDMcg : BigDecimal.ZERO;
        this.ironMg = ironMg != null ? ironMg : BigDecimal.ZERO;
        this.calciumMg = calciumMg != null ? calciumMg : BigDecimal.ZERO;
        this.potassiumMg = potassiumMg != null ? potassiumMg : BigDecimal.ZERO;
    }

    // --- GETTERS ---
    public BigDecimal getCalories() { return calories; }
    public BigDecimal getProteinG() { return proteinG; }
    public BigDecimal getCarbsG() { return carbsG; }
    public BigDecimal getFatG() { return fatG; }
    public BigDecimal getFiberG() { return fiberG; }
    public BigDecimal getVitaminAMcg() { return vitaminAMcg; }
    public BigDecimal getVitaminB12Mcg() { return vitaminB12Mcg; }
    public BigDecimal getVitaminCMg() { return vitaminCMg; }
    public BigDecimal getVitaminDMcg() { return vitaminDMcg; }
    public BigDecimal getIronMg() { return ironMg; }
    public BigDecimal getCalciumMg() { return calciumMg; }
    public BigDecimal getPotassiumMg() { return potassiumMg; }
}