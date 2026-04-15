package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "scan_history")
public class ScanHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scan_id") private Long scanId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "scan_type", nullable = false) private String scanType;
    @Column(name = "scan_value") private String scanValue;
    @Column(name = "result_food_id") private Long resultFoodId;
    @Column(name = "is_success") private Boolean isSuccess = false;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
    public Long getScanId() {
        return scanId;
    }

    public void setScanId(Long scanId) {
        this.scanId = scanId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getScanValue() {
        return scanValue;
    }

    public void setScanValue(String scanValue) {
        this.scanValue = scanValue;
    }

    public Long getResultFoodId() {
        return resultFoodId;
    }

    public void setResultFoodId(Long resultFoodId) {
        this.resultFoodId = resultFoodId;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}