package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity @Table(name = "daily_deficits")
public class DailyDeficit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deficit_id") private Long deficitId;
    @Column(name = "summary_id", nullable = false) private Long summaryId;
    @Column(name = "nutrient_id", nullable = false) private Integer nutrientId;
    @Column(name = "deficit_amount", precision = 8, scale = 2) private BigDecimal deficitAmount;


    public BigDecimal getDeficitAmount() {
        return deficitAmount;
    }

    public void setDeficitAmount(BigDecimal deficitAmount) {
        this.deficitAmount = deficitAmount;
    }

    public Long getDeficitId() {
        return deficitId;
    }

    public void setDeficitId(Long deficitId) {
        this.deficitId = deficitId;
    }

    public Integer getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(Integer nutrientId) {
        this.nutrientId = nutrientId;
    }

    public Long getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }

}