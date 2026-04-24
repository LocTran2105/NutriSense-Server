package hcmute.edu.vn.nitrisensebackend.controller;

import hcmute.edu.vn.nitrisensebackend.dto.DashboardDTO;
import hcmute.edu.vn.nitrisensebackend.entity.DailySummary;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.User;
import hcmute.edu.vn.nitrisensebackend.repository.DailySummaryRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryItemRepository;
import hcmute.edu.vn.nitrisensebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    @Autowired
    private FoodEntryItemRepository foodEntryItemRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardReport(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DailySummary> summaries = dailySummaryRepository.findByUserIdAndSummaryDateBetweenOrderBySummaryDateAsc(userId, startDate, endDate);
        Map<LocalDate, DailySummary> map = summaries.stream()
                .collect(Collectors.toMap(DailySummary::getSummaryDate, s -> s));

        List<DashboardDTO.ChartItemDTO> chartData = new ArrayList<>();
        int totalCal = 0, totalWater = 0;
        double totalPro = 0.0, totalCarb = 0.0, totalFat = 0.0;

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            DailySummary ds = map.get(current);
            DashboardDTO.ChartItemDTO item = new DashboardDTO.ChartItemDTO();
            item.setDate(current.toString());

            if (ds != null) {
                int cal = ds.getTotalCalories() != null ? ds.getTotalCalories() : 0;
                item.setCalories(cal);
                totalCal += cal;
                totalWater += ds.getTotalWaterMl() != null ? ds.getTotalWaterMl() : 0;

                totalPro += ds.getTotalProteinG() != null ? ds.getTotalProteinG().doubleValue() : 0.0;
                totalCarb += ds.getTotalCarbsG() != null ? ds.getTotalCarbsG().doubleValue() : 0.0;
                totalFat += ds.getTotalFatG() != null ? ds.getTotalFatG().doubleValue() : 0.0;
            } else {
                item.setCalories(0);
            }
            chartData.add(item);
            current = current.plusDays(1);
        }

        List<FoodEntryItem> allItems = foodEntryItemRepository.findAllItemsByUserIdAndDateRange(userId, startDate, endDate);
        double totalFiber = 0.0, totalVitC = 0.0, totalIron = 0.0, totalCalcium = 0.0;

        for (FoodEntryItem item : allItems) {
            totalFiber += item.getFiberG() != null ? item.getFiberG().doubleValue() : 0.0;
            totalVitC += item.getVitaminCMg() != null ? item.getVitaminCMg().doubleValue() : 0.0;
            totalIron += item.getIronMg() != null ? item.getIronMg().doubleValue() : 0.0;
            totalCalcium += item.getCalciumMg() != null ? item.getCalciumMg().doubleValue() : 0.0;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        User user = userRepository.findById(userId).orElse(null);
        int dailyTargetCal = (user != null && user.getDailyCalorieGoal() != null) ? user.getDailyCalorieGoal() : 2000;
        int dailyTargetWater = (user != null && user.getWaterGoalMl() != null) ? user.getWaterGoalMl() : 2000;

        double dailyTargetPro = (dailyTargetCal * 0.3) / 4.0;
        double dailyTargetCarb = (dailyTargetCal * 0.4) / 4.0;
        double dailyTargetFat = (dailyTargetCal * 0.3) / 9.0;

        DashboardDTO dto = new DashboardDTO();

        dto.setAvgCalories((int) (totalCal / totalDays));
        dto.setAvgWaterLiters((totalWater / (double) totalDays) / 1000.0);
        dto.setAvgProtein(totalPro / totalDays);
        dto.setAvgCarbs(totalCarb / totalDays);
        dto.setAvgFat(totalFat / totalDays);
        dto.setAvgFiber(totalFiber / totalDays);
        dto.setAvgVitaminC(totalVitC / totalDays);
        dto.setAvgIron(totalIron / totalDays);
        dto.setAvgCalcium(totalCalcium / totalDays);

        dto.setTotalCalories(totalCal);
        dto.setTargetCalories((int)(dailyTargetCal * totalDays));

        dto.setTotalWaterLiters(totalWater / 1000.0);
        dto.setTargetWaterLiters((dailyTargetWater * totalDays) / 1000.0);

        dto.setTotalProtein(totalPro);
        dto.setTargetProtein(dailyTargetPro * totalDays);

        dto.setTotalCarbs(totalCarb);
        dto.setTargetCarbs(dailyTargetCarb * totalDays);

        dto.setTotalFat(totalFat);
        dto.setTargetFat(dailyTargetFat * totalDays);

        dto.setTotalFiber(totalFiber);
        dto.setTargetFiber(28.0 * totalDays);

        dto.setTotalVitaminC(totalVitC);
        dto.setTargetVitaminC(90.0 * totalDays);

        dto.setTotalIron(totalIron);
        dto.setTargetIron(18.0 * totalDays);

        dto.setTotalCalcium(totalCalcium);
        dto.setTargetCalcium(1000.0 * totalDays);

        dto.setChartData(chartData);

        return ResponseEntity.ok(dto);
    }
}