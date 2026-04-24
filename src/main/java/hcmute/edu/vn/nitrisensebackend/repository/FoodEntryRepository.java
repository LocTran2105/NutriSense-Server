package hcmute.edu.vn.nitrisensebackend.repository;

import hcmute.edu.vn.nitrisensebackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {

    List<FoodEntry> findByUserIdAndEntryDateAndIsDeletedFalse(Long userId, LocalDate entryDate);

    Optional<FoodEntry> findByUserIdAndEntryDateAndMealTypeAndIsDeletedFalse(Long userId, LocalDate entryDate, String mealType);

    @Query("SELECT COUNT(DISTINCT e.entryId) FROM FoodEntry e " +
            "WHERE e.userId = :userId AND e.entryDate = :date AND e.isDeleted = false")
    Integer countValidMealsByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT e.userId FROM FoodEntry e WHERE e.entryDate = :date")
    List<Long> findActiveUsersByDate(@Param("date") LocalDate date);
}