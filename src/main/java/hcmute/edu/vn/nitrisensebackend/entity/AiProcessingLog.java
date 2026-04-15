package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "ai_processing_logs")
public class AiProcessingLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id") private Long logId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "input_type", nullable = false) private String inputType;
    @Column(name = "raw_input", nullable = false, columnDefinition = "TEXT") private String rawInput;
    @Column(name = "ai_response_json", columnDefinition = "JSON") private String aiResponseJson;
    @Column(name = "confidence_score", precision = 3, scale = 2) private BigDecimal confidenceScore;
    @Column(name = "parsed_nutrients", columnDefinition = "JSON") private String parsedNutrients;
    @Column(name = "error_message", columnDefinition = "TEXT") private String errorMessage;
    @Column(name = "processing_time_ms") private Integer processingTimeMs;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;

    public String getAiResponseJson() {
        return aiResponseJson;
    }

    public void setAiResponseJson(String aiResponseJson) {
        this.aiResponseJson = aiResponseJson;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getParsedNutrients() {
        return parsedNutrients;
    }

    public void setParsedNutrients(String parsedNutrients) {
        this.parsedNutrients = parsedNutrients;
    }

    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getRawInput() {
        return rawInput;
    }

    public void setRawInput(String rawInput) {
        this.rawInput = rawInput;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}