package hcmute.edu.vn.nitrisensebackend.repository;

import hcmute.edu.vn.nitrisensebackend.entity.FoodItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findTop5ByNameContainingIgnoreCase(String keyword);

    @Query("SELECT f FROM FoodItem f WHERE f.isDeleted = false AND f.proteinG >= 15.0 ORDER BY f.proteinG DESC")
    List<FoodItem> findTopProteinFoods(Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.isDeleted = false AND f.vitaminCMg >= 40.0 ORDER BY f.vitaminCMg DESC")
    List<FoodItem> findTopVitaminCFoods(Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.isDeleted = false AND f.ironMg >= 2.0 ORDER BY f.ironMg DESC")
    List<FoodItem> findTopIronFoods(Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.isDeleted = false AND f.calciumMg >= 100.0 ORDER BY f.calciumMg DESC")
    List<FoodItem> findTopCalciumFoods(Pageable pageable);
}