package hcmute.edu.vn.nitrisensebackend.repository;

import hcmute.edu.vn.nitrisensebackend.entity.ChatMessage;
import hcmute.edu.vn.nitrisensebackend.enums.ChatSender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUserIdOrderByCreatedAtAsc(Long userId);

    List<ChatMessage> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalseAndSenderIn(Long userId, List<ChatSender> senders);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage c SET c.isRead = true, c.readAt = CURRENT_TIMESTAMP WHERE c.userId = :userId AND c.isRead = false AND c.sender IN :senders")
    void markUnreadAsRead(@Param("userId") Long userId, @Param("senders") List<ChatSender> senders);
}