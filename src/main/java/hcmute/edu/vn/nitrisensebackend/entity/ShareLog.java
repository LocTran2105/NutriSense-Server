package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "share_logs")
public class ShareLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id") private Long shareId;
    @Column(name = "report_id", nullable = false) private Long reportId;
    @Column(name = "shared_to_email", nullable = false) private String sharedToEmail;
    @Column(name = "shared_at", insertable = false, updatable = false) private LocalDateTime sharedAt;
    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getSharedToEmail() {
        return sharedToEmail;
    }

    public void setSharedToEmail(String sharedToEmail) {
        this.sharedToEmail = sharedToEmail;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}
