package hcmute.edu.vn.nitrisensebackend.repository;

import hcmute.edu.vn.nitrisensebackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiProcessingLogRepository extends JpaRepository<AiProcessingLog, Long> {
    List<AiProcessingLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
