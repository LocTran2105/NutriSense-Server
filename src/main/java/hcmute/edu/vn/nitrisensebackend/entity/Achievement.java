package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "achievements")
public class Achievement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id") private Long achievementId;
    @Column(nullable = false, length = 100) private String name;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "badge_icon_url", columnDefinition = "TEXT") private String badgeIconUrl;
    @Column(name = "requirement_type") private String requirementType;
    @Column(name = "requirement_value", nullable = false) private Integer requirementValue;
    @Column(name = "points") private Integer points = 0;

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    public String getBadgeIconUrl() {
        return badgeIconUrl;
    }

    public void setBadgeIconUrl(String badgeIconUrl) {
        this.badgeIconUrl = badgeIconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public Integer getRequirementValue() {
        return requirementValue;
    }

    public void setRequirementValue(Integer requirementValue) {
        this.requirementValue = requirementValue;
    }

}