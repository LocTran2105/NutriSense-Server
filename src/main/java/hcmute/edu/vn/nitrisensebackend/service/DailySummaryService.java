package hcmute.edu.vn.nitrisensebackend.service;

import hcmute.edu.vn.nitrisensebackend.dto.NutrientSumDto;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.repository.DailySummaryRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryItemRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

        NutrientSumDto nutSum = itemRepo.getDailyNutrientSum(userId, date);

        if (nutSum != null) {
            summary.setTotalCalories(nutSum.getCalories() != null ? nutSum.getCalories().intValue() : 0);
            summary.setTotalProteinG(nutSum.getProteinG());
            summary.setTotalCarbsG(nutSum.getCarbsG());
            summary.setTotalFatG(nutSum.getFatG());

        } else {
            summary.setTotalCalories(0);
            summary.setTotalProteinG(null);
            summary.setTotalCarbsG(null);
            summary.setTotalFatG(null);
        }

        Integer validMeals = entryRepo.countValidMealsByUserAndDate(userId, date);
        summary.setNumMeals(validMeals != null ? validMeals : 0);

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