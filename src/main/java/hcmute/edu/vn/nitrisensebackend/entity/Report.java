package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "reports")
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id") private Long reportId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Column(name = "total_days", nullable = false) private Integer totalDays;
    @Column(name = "compliance_score", precision = 5, scale = 2) private BigDecimal complianceScore;
    @Column(name = "avg_calories", precision = 8, scale = 2) private BigDecimal avgCalories;
    @Column(name = "avg_water_ml", precision = 8, scale = 2) private BigDecimal avgWaterMl;
    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT") private String summaryText;
    @Column(name = "recommendation", columnDefinition = "TEXT") private String recommendation;
    @Column(name = "chart_image_url", columnDefinition = "TEXT") private String chartImageUrl;
    @Column(name = "shared_count") private Integer sharedCount = 0;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getComplianceScore() {
        return complianceScore;
    }

    public void setComplianceScore(BigDecimal complianceScore) {
        this.complianceScore = complianceScore;
    }

    public BigDecimal getAvgCalories() {
        return avgCalories;
    }

    public void setAvgCalories(BigDecimal avgCalories) {
        this.avgCalories = avgCalories;
    }

    public BigDecimal getAvgWaterMl() {
        return avgWaterMl;
    }

    public void setAvgWaterMl(BigDecimal avgWaterMl) {
        this.avgWaterMl = avgWaterMl;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getChartImageUrl() {
        return chartImageUrl;
    }

    public void setChartImageUrl(String chartImageUrl) {
        this.chartImageUrl = chartImageUrl;
    }

    public Integer getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(Integer sharedCount) {
        this.sharedCount = sharedCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
