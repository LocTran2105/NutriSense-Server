package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "water_intake")
public class WaterIntake {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "water_id") private Long waterId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "intake_date", nullable = false) private LocalDate intakeDate;
    @Column(name = "amount_ml", nullable = false) private Integer amountMl;
    @Column(name = "recorded_at") private LocalDateTime recordedAt;
    @Column(name = "source") private String source;
    public Long getWaterId() {
        return waterId;
    }

    public void setWaterId(Long waterId) {
        this.waterId = waterId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(LocalDate intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Integer getAmountMl() {
        return amountMl;
    }

    public void setAmountMl(Integer amountMl) {
        this.amountMl = amountMl;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}