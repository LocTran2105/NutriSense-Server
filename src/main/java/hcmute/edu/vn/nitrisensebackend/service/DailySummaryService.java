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

        // 1. Lấy hoặc tạo mới DailySummary
        DailySummary summary = summaryRepo.findByUserIdAndSummaryDate(userId, date)
                .orElseGet(() -> new DailySummary(userId, date));

        // 2. Tính tổng dinh dưỡng từ Database (Chỉ lấy Calo & Macro)
        NutrientSumDto nutSum = itemRepo.getDailyNutrientSum(userId, date);

        if (nutSum != null) {
            summary.setTotalCalories(nutSum.getCalories() != null ? nutSum.getCalories().intValue() : 0);
            summary.setTotalProteinG(nutSum.getProteinG());
            summary.setTotalCarbsG(nutSum.getCarbsG());
            summary.setTotalFatG(nutSum.getFatG());

            // ❌ KHÔNG GỌI SET CÁC VI CHẤT VÌ ĐÃ BỎ KHỎI BẢNG ĐỂ TRÁNH LỖI DATABASE
        } else {
            // Nếu xóa hết món ăn, trả số liệu về 0
            summary.setTotalCalories(0);
            summary.setTotalProteinG(null);
            summary.setTotalCarbsG(null);
            summary.setTotalFatG(null);
        }

        // 3. Đếm số bữa ăn hợp lệ (Count Distinct)
        Integer validMeals = entryRepo.countValidMealsByUserAndDate(userId, date);
        summary.setNumMeals(validMeals != null ? validMeals : 0);

        // 4. Lưu xuống DB
        summaryRepo.save(summary);
    }

    // Dùng khi User đổi ngày của một bữa ăn (để ngày cũ không bị mồ côi)
    @Transactional
    public void handleDateChange(Long userId, LocalDate oldDate, LocalDate newDate) {
        if (oldDate != null && !oldDate.equals(newDate)) {
            recalculateDailySummary(userId, oldDate);
        }
        recalculateDailySummary(userId, newDate);
    }

    // FIX LỖI 3: Thêm hàm lấy dữ liệu dinh dưỡng theo ngày để phục vụ AI RAG
    public hcmute.edu.vn.nitrisensebackend.entity.DailySummary getSummaryByDate(Long userId, java.time.LocalDate date) {
        // Đã sửa 'dailySummaryRepository' thành 'summaryRepo' cho khớp với khai báo ở đầu file
        return summaryRepo.findByUserIdAndSummaryDate(userId, date).orElse(null);
    }
}