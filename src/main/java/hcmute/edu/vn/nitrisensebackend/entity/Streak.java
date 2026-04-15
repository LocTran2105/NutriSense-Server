package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "streaks")
public class Streak {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streak_id") private Long streakId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "streak_type", nullable = false) private String streakType;
    @Column(name = "current_streak") private Integer currentStreak = 0;
    @Column(name = "longest_streak") private Integer longestStreak = 0;
    @Column(name = "last_updated_date") private LocalDate lastUpdatedDate;
    @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime updatedAt;

    public Long getStreakId() {
        return streakId;
    }

    public void setStreakId(Long streakId) {
        this.streakId = streakId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStreakType() {
        return streakType;
    }

    public void setStreakType(String streakType) {
        this.streakType = streakType;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
