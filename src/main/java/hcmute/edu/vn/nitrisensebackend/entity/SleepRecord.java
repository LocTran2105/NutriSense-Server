package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "sleep_records")
public class SleepRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_id") private Long sleepId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "sleep_start", nullable = false) private LocalDateTime sleepStart;
    @Column(name = "sleep_end", nullable = false) private LocalDateTime sleepEnd;
    @Column(name = "duration_hours", insertable = false, updatable = false) private BigDecimal durationHours;
    @Column(name = "quality") private String quality;
    @Column(columnDefinition = "TEXT") private String notes;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
    public Long getSleepId() {
        return sleepId;
    }

    public void setSleepId(Long sleepId) {
        this.sleepId = sleepId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public void setSleepStart(LocalDateTime sleepStart) {
        this.sleepStart = sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public void setSleepEnd(LocalDateTime sleepEnd) {
        this.sleepEnd = sleepEnd;
    }

    public BigDecimal getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(BigDecimal durationHours) {
        this.durationHours = durationHours;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
