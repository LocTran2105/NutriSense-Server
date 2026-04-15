package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "exercise_tests")
public class ExerciseTest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id") private Long testId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "test_date", nullable = false) private LocalDate testDate;
    @Column(name = "test_type", nullable = false) private String testType;
    @Column(nullable = false, precision = 8, scale = 2) private BigDecimal value;
    @Column(nullable = false, length = 20) private String unit;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
