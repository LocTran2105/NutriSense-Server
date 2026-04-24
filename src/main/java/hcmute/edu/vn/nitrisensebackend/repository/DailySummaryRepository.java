package hcmute.edu.vn.nitrisensebackend.repository;

import hcmute.edu.vn.nitrisensebackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {
    Optional<DailySummary> findByUserIdAndSummaryDate(Long userId, LocalDate summaryDate);

    List<DailySummary> findByUserIdAndSummaryDateBetweenOrderBySummaryDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);
}
