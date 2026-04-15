package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "user_achievements")
@IdClass(UserAchievementId.class)
public class UserAchievement {
    @Id @Column(name = "user_id") private Long userId;
    @Id @Column(name = "achievement_id") private Long achievementId;
    @Column(name = "achieved_at", insertable = false, updatable = false) private LocalDateTime achievedAt;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    public LocalDateTime getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(LocalDateTime achievedAt) {
        this.achievedAt = achievedAt;
    }
}