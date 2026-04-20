package hcmute.edu.vn.nitrisensebackend.service;

import hcmute.edu.vn.nitrisensebackend.dto.NutrientDto;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntry;
import hcmute.edu.vn.nitrisensebackend.entity.FoodEntryItem;
import hcmute.edu.vn.nitrisensebackend.entity.FoodItem;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryItemRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodEntryRepository;
import hcmute.edu.vn.nitrisensebackend.repository.FoodItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {

    @Autowired private FoodItemRepository foodItemRepository;
    @Autowired private FoodEntryRepository foodEntryRepository;
    @Autowired private FoodEntryItemRepository foodEntryItemRepository;

    // TIÊM TRUNG TÂM TÍNH TOÁN
    @Autowired private DailySummaryService dailySummaryService;

    // 1. LƯU 1 MÓN (DÙNG CHO TEXT HOẶC NHẬP TAY)
    @Transactional
    public FoodEntryItem saveMealData(Long userId, String mealType, String userInput, FoodItem foodItem, String source, String imageUrls) {

        foodItem.setDeleted(false); // THÊM DÒNG NÀY
        foodItem = foodItemRepository.save(foodItem);

        LocalDate today = LocalDate.now();
        Optional<FoodEntry> existingEntry = foodEntryRepository.findByUserIdAndEntryDateAndMealTypeAndIsDeletedFalse(userId, today, mealType);

        FoodEntry currentEntry = existingEntry.orElseGet(() -> {
            FoodEntry newEntry = new FoodEntry();
            newEntry.setUserId(userId);
            newEntry.setEntryDate(today);
            newEntry.setMealType(mealType);
            newEntry.setCreatedBy(userId);

            newEntry.setDeleted(false); // THÊM DÒNG NÀY QUAN TRỌNG NHẤT

            return foodEntryRepository.save(newEntry);
        });

        FoodEntryItem entryItem = new FoodEntryItem();
        entryItem.setEntryId(currentEntry.getEntryId());
        entryItem.setFoodId(foodItem.getFoodId());
        entryItem.setServingQty(BigDecimal.ONE);
        entryItem.setServingUnit(foodItem.getServingUnit() != null ? foodItem.getServingUnit() : "phần");

        entryItem.setCaloriesPerServing(foodItem.getCalories());
        entryItem.setProteinG(foodItem.getProteinG());
        entryItem.setCarbsG(foodItem.getCarbsG());
        entryItem.setFatG(foodItem.getFatG());
        entryItem.setFiberG(foodItem.getFiberG());
        entryItem.setVitaminAMcg(foodItem.getVitaminAMcg());
        entryItem.setVitaminB12Mcg(foodItem.getVitaminB12Mcg());
        entryItem.setVitaminCMg(foodItem.getVitaminCMg());
        entryItem.setVitaminDMcg(foodItem.getVitaminDMcg());
        entryItem.setIronMg(foodItem.getIronMg());
        entryItem.setCalciumMg(foodItem.getCalciumMg());
        entryItem.setPotassiumMg(foodItem.getPotassiumMg());

        entryItem.setSource(source != null ? source : "text");
        entryItem.setRawInput(userInput);
        if (imageUrls != null) {
            entryItem.setImageUrls(imageUrls);
        }

        entryItem.setDeleted(false); // THÊM DÒNG NÀY

        FoodEntryItem savedItem = foodEntryItemRepository.save(entryItem);

        dailySummaryService.recalculateDailySummary(userId, today);
        return savedItem;
    }

    // 2. LƯU HÀNG LOẠT MÓN TỪ ẢNH (BẬT TRANSACTION BẢO VỆ)
        @Transactional(rollbackOn = Exception.class)
        public void saveMealFromAi(Long userId, String mealType, LocalDate date, List<NutrientDto> confirmedItems, String imageUrls) {

            FoodEntry currentEntry = foodEntryRepository.findByUserIdAndEntryDateAndMealTypeAndIsDeletedFalse(userId, date, mealType)
                    .orElseGet(() -> {
                        FoodEntry newEntry = new FoodEntry();
                        newEntry.setUserId(userId);
                        newEntry.setEntryDate(date);
                        newEntry.setMealType(mealType);
                        newEntry.setCreatedBy(userId);

                        newEntry.setDeleted(false); // THÊM DÒNG NÀY

                        return foodEntryRepository.save(newEntry);
                    });

            List<FoodEntryItem> itemsToSave = new ArrayList<>();

            for (NutrientDto dto : confirmedItems) {
                FoodItem foodItem = new FoodItem();
                foodItem.setName(dto.getFoodName() != null ? dto.getFoodName() : "Món ăn từ ảnh");
                foodItem.setServingSize(dto.getServingSize() != null ? dto.getServingSize() : BigDecimal.ONE);
                foodItem.setServingUnit(dto.getServingUnit() != null ? dto.getServingUnit() : "phần");

                foodItem.setCalories(safe(dto.getCalories()));
                foodItem.setProteinG(safe(dto.getProteinG()));
                foodItem.setCarbsG(safe(dto.getCarbsG()));
                foodItem.setFatG(safe(dto.getFatG()));
                foodItem.setFiberG(safe(dto.getFiberG()));
                foodItem.setVitaminAMcg(safe(dto.getVitaminAMcg()));
                foodItem.setVitaminB12Mcg(safe(dto.getVitaminB12Mcg()));
                foodItem.setVitaminCMg(safe(dto.getVitaminCMg()));
                foodItem.setVitaminDMcg(safe(dto.getVitaminDMcg()));
                foodItem.setIronMg(safe(dto.getIronMg()));
                foodItem.setCalciumMg(safe(dto.getCalciumMg()));
                foodItem.setPotassiumMg(safe(dto.getPotassiumMg()));

                foodItem.setSource("gemini_ai");
                foodItem.setCreatedBy(userId);

                foodItem.setDeleted(false); // THÊM DÒNG NÀY

                foodItem = foodItemRepository.save(foodItem);

                FoodEntryItem entryItem = new FoodEntryItem();
                entryItem.setEntryId(currentEntry.getEntryId());
                entryItem.setFoodId(foodItem.getFoodId());
                entryItem.setServingQty(BigDecimal.ONE);
                if (dto.getRawInput() != null && !dto.getRawInput().trim().isEmpty()) {
                    entryItem.setRawInput(dto.getRawInput());
                    entryItem.setCustomName(dto.getRawInput());
                } else {
                    // Đề phòng Android không gửi rawInput, ta lấy luôn tên foodName
                    entryItem.setRawInput(dto.getFoodName() != null ? dto.getFoodName() : "Món ăn từ ảnh");
                    entryItem.setCustomName(dto.getFoodName() != null ? dto.getFoodName() : "Món ăn từ ảnh");
                }
                entryItem.setCaloriesPerServing(foodItem.getCalories());
                entryItem.setProteinG(foodItem.getProteinG());
                entryItem.setCarbsG(foodItem.getCarbsG());
                entryItem.setFatG(foodItem.getFatG());
                entryItem.setFiberG(foodItem.getFiberG());
                entryItem.setVitaminAMcg(foodItem.getVitaminAMcg());
                entryItem.setVitaminB12Mcg(foodItem.getVitaminB12Mcg());
                entryItem.setVitaminCMg(foodItem.getVitaminCMg());
                entryItem.setVitaminDMcg(foodItem.getVitaminDMcg());
                entryItem.setIronMg(foodItem.getIronMg());
                entryItem.setCalciumMg(foodItem.getCalciumMg());
                entryItem.setPotassiumMg(foodItem.getPotassiumMg());

                entryItem.setSource("image");
                if (imageUrls != null) {
                    entryItem.setImageUrls(imageUrls);
                }

                entryItem.setDeleted(false); // THÊM DÒNG NÀY

                itemsToSave.add(entryItem);
            }

            foodEntryItemRepository.saveAll(itemsToSave);
            dailySummaryService.recalculateDailySummary(userId, date);
        }

    public List<FoodEntryItem> getMealItems(Long userId, LocalDate date, String mealType) {
        Optional<FoodEntry> entry = foodEntryRepository
                .findByUserIdAndEntryDateAndMealTypeAndIsDeletedFalse(userId, date, mealType);

        if (entry.isPresent()) {
            return foodEntryItemRepository.findByEntryIdAndIsDeletedFalse(entry.get().getEntryId());
        }
        return new ArrayList<>();
    }

    @Transactional
    public void deleteWholeMeal(Long userId, LocalDate date, String mealType) {
        Optional<FoodEntry> entry = foodEntryRepository
                .findByUserIdAndEntryDateAndMealTypeAndIsDeletedFalse(userId, date, mealType);

        if (entry.isPresent()) {
            FoodEntry e = entry.get();
            e.setDeleted(true);
            foodEntryRepository.save(e);

            List<FoodEntryItem> items = foodEntryItemRepository.findByEntryIdAndIsDeletedFalse(e.getEntryId());
            for(FoodEntryItem item : items) {
                item.setDeleted(true);
                foodEntryItemRepository.save(item);
            }

            // CẬP NHẬT TỔNG KẾT NGÀY
            dailySummaryService.recalculateDailySummary(userId, date);
        }
    }

    @Transactional
    public void deleteMealItem(Long itemId) {
        Optional<FoodEntryItem> itemOpt = foodEntryItemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            FoodEntryItem item = itemOpt.get();
            item.setDeleted(true);
            foodEntryItemRepository.save(item);

            // Tìm ngày của món ăn để cập nhật
            foodEntryRepository.findById(item.getEntryId()).ifPresent(entry -> {
                dailySummaryService.recalculateDailySummary(entry.getUserId(), entry.getEntryDate());
            });
        }
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
    // ==========================================================
    // LẤY TẤT CẢ MÓN ĂN TRONG NGÀY (SẠCH - KHÔNG BỊ XÓA MỀM)
    // ==========================================================
    public List<FoodEntryItem> getMealItemsForWholeDay(Long userId, LocalDate date) {
        List<FoodEntryItem> allItemsToday = new ArrayList<>();

        // Lấy tất cả các bữa ăn (Sáng, Trưa, Tối, Phụ...) chưa bị xóa
        // LƯU Ý: Bạn cần mở file FoodEntryRepository.java và thêm dòng này vào interface:
        // List<FoodEntry> findByUserIdAndEntryDateAndIsDeletedFalse(Long userId, LocalDate date);
        List<FoodEntry> validEntries = foodEntryRepository.findByUserIdAndEntryDateAndIsDeletedFalse(userId, date);

        if (validEntries != null) {
            for (FoodEntry entry : validEntries) {
                // Chỉ lấy món ăn chưa bị xóa bên trong bữa ăn hợp lệ
                allItemsToday.addAll(foodEntryItemRepository.findByEntryIdAndIsDeletedFalse(entry.getEntryId()));
            }
        }
        return allItemsToday;
    }
}