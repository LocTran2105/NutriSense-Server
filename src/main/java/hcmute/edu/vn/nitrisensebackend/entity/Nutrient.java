package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity @Table(name = "nutrients")
public class Nutrient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutrient_id") private Integer nutrientId;
    @Column(name = "nutrient_name", nullable = false, unique = true, length = 50) private String nutrientName;
    @Column(length = 20) private String unit;

    public Integer getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(Integer nutrientId) {
        this.nutrientId = nutrientId;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
