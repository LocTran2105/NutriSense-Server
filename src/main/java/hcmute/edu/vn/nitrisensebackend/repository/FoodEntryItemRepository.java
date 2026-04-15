package hcmute.edu.vn.nitrisensebackend.repository;

import hcmute.edu.vn.nitrisensebackend.entity.*;
import hcmute.edu.vn.nitrisensebackend.dto.NutrientSumDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FoodEntryItemRepository extends JpaRepository<FoodEntryItem, Long> {

    List<FoodEntryItem> findByEntryIdAndIsDeletedFalse(Long entryId);

    // Dành cho hiển thị báo cáo hoặc lịch sử
    @Query(value = "SELECT i.* FROM food_entry_items i " +
            "INNER JOIN food_entries e ON i.entry_id = e.entry_id " +
            "WHERE e.user_id = :userId " +
            "AND e.entry_date BETWEEN :startDate AND :endDate " +
            "AND i.is_deleted = false AND e.is_deleted = false",
            nativeQuery = true)
    List<FoodEntryItem> findAllItemsByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // VŨ KHÍ TỐI THƯỢNG 2: Ép MySQL tính tổng dinh dưỡng cực nhanh
    @Query("SELECT new hcmute.edu.vn.nitrisensebackend.dto.NutrientSumDto(" +
            "SUM(i.calories), SUM(i.proteinG), SUM(i.carbsG), SUM(i.fatG), " +
            "SUM(i.fiberG), SUM(i.vitaminAMcg), SUM(i.vitaminB12Mcg), SUM(i.vitaminCMg), " +
            "SUM(i.vitaminDMcg), SUM(i.ironMg), SUM(i.calciumMg), SUM(i.potassiumMg)) " +
            "FROM FoodEntryItem i JOIN FoodEntry e ON i.entryId = e.entryId " +
            "WHERE e.userId = :userId AND e.entryDate = :date " +
            "AND i.isDeleted = false AND e.isDeleted = false")
    NutrientSumDto getDailyNutrientSum(@Param("userId") Long userId, @Param("date") LocalDate date);
}