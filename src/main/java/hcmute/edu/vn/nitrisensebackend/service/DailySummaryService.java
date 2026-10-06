package hcmute.edu.vn.nitrisensebackend.service;

import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntry;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.repository.DailySummaryRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryItemRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DailySummaryService {

    @Autowired private DailySummaryRepository summaryRepo;
    @Autowired private FoodEntryItemRepository itemRepo;
    @Autowired private FoodEntryRepository entryRepo;

    @Transactional
    public void recalculateDailySummary(Long userId, LocalDate date) {
        if (date == null) return;

        DailySummary summary = summaryRepo.findByUserIdAndSummaryDate(userId, date)
                .orElseGet(() -> new DailySummary(userId, date));

        // Lấy tất cả các bữa ăn hợp lệ trong ngày của User
        List<FoodEntry> entries = entryRepo.findByUserIdAndEntryDateAndIsDeletedFalse(userId, date);

        double totalCal = 0;
        double totalPro = 0;
        double totalCarb = 0;
        double totalFat = 0;

        if (entries != null && !entries.isEmpty()) {
            for (FoodEntry entry : entries) {
                // Lấy tất cả món ăn trong từng bữa
                List<FoodEntryItem> items = itemRepo.findByEntryIdAndIsDeletedFalse(entry.getEntryId());

                for (FoodEntryItem item : items) {
                    BigDecimal calPerServing = item.getCaloriesPerServing() != null ? item.getCaloriesPerServing() : BigDecimal.ZERO;
                    BigDecimal qty = item.getServingQty() != null ? item.getServingQty() : BigDecimal.ONE;

                    // Tính Calo = CaloriesPerServing * ServingQty
                    totalCal += calPerServing.multiply(qty).doubleValue();

                    // Cộng dồn Macro
                    totalPro += item.getProteinG() != null ? item.getProteinG().doubleValue() : 0;
                    totalCarb += item.getCarbsG() != null ? item.getCarbsG().doubleValue() : 0;
                    totalFat += item.getFatG() != null ? item.getFatG().doubleValue() : 0;
                }
            }
        }

        // Cập nhật lại vào đối tượng summary
        summary.setTotalCalories((int) Math.round(totalCal));
        summary.setTotalProteinG(BigDecimal.valueOf(totalPro));
        summary.setTotalCarbsG(BigDecimal.valueOf(totalCarb));
        summary.setTotalFatG(BigDecimal.valueOf(totalFat));

        // Cập nhật số bữa ăn
        Integer validMeals = entryRepo.countValidMealsByUserAndDate(userId, date);
        summary.setNumMeals(validMeals != null ? validMeals : 0);

        // Lưu vào Database
        summaryRepo.save(summary);
    }

    @Transactional
    public void handleDateChange(Long userId, LocalDate oldDate, LocalDate newDate) {
        if (oldDate != null && !oldDate.equals(newDate)) {
            recalculateDailySummary(userId, oldDate);
        }
        recalculateDailySummary(userId, newDate);
    }

    public hcmute.edu.vn.nitrisensebackend.entity.DailySummary getSummaryByDate(Long userId, java.time.LocalDate date) {
        return summaryRepo.findByUserIdAndSummaryDate(userId, date).orElse(null);
    }
}