    package hcmute.edu.vn.nitrisensebackend.dto;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import java.math.BigDecimal;
    @JsonIgnoreProperties(ignoreUnknown = true) // <-- DÒNG QUAN TRỌNG ĐỂ CHỐNG CRASH
    public class NutrientDto {

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public BigDecimal getServingSize() {
            return servingSize;
        }

        public void setServingSize(BigDecimal servingSize) {
            this.servingSize = servingSize;
        }

        public String getServingUnit() {
            return servingUnit;
        }

        public void setServingUnit(String servingUnit) {
            this.servingUnit = servingUnit;
        }

        public BigDecimal getCalories() {
            return calories;
        }

        public void setCalories(BigDecimal calories) {
            this.calories = calories;
        }

        public BigDecimal getProteinG() {
            return proteinG;
        }

        public void setProteinG(BigDecimal proteinG) {
            this.proteinG = proteinG;
        }

        public BigDecimal getCarbsG() {
            return carbsG;
        }

        public void setCarbsG(BigDecimal carbsG) {
            this.carbsG = carbsG;
        }

        public BigDecimal getFatG() {
            return fatG;
        }

        public void setFatG(BigDecimal fatG) {
            this.fatG = fatG;
        }

        public BigDecimal getVitaminCMg() {
            return vitaminCMg;
        }

        public void setVitaminCMg(BigDecimal vitaminCMg) {
            this.vitaminCMg = vitaminCMg;
        }

        public BigDecimal getCalciumMg() {
            return calciumMg;
        }

        public void setCalciumMg(BigDecimal calciumMg) {
            this.calciumMg = calciumMg;
        }

        public BigDecimal getFiberG() {
            return fiberG;
        }

        public void setFiberG(BigDecimal fiberG) {
            this.fiberG = fiberG;
        }

        public BigDecimal getVitaminAMcg() {
            return vitaminAMcg;
        }

        public void setVitaminAMcg(BigDecimal vitaminAMcg) {
            this.vitaminAMcg = vitaminAMcg;
        }

        public BigDecimal getVitaminB12Mcg() {
            return vitaminB12Mcg;
        }

        public void setVitaminB12Mcg(BigDecimal vitaminB12Mcg) {
            this.vitaminB12Mcg = vitaminB12Mcg;
        }

        public BigDecimal getVitaminDMcg() {
            return vitaminDMcg;
        }

        public void setVitaminDMcg(BigDecimal vitaminDMcg) {
            this.vitaminDMcg = vitaminDMcg;
        }

        public BigDecimal getIronMg() {
            return ironMg;
        }

        public void setIronMg(BigDecimal ironMg) {
            this.ironMg = ironMg;
        }

        public BigDecimal getPotassiumMg() {
            return potassiumMg;
        }

        public void setPotassiumMg(BigDecimal potassiumMg) {
            this.potassiumMg = potassiumMg;
        }

        // @JsonProperty giúp Java biết chính xác key nào trong JSON sẽ chui vào biến nào
        @JsonProperty("food_name")
        private String foodName;

        @JsonProperty("serving_size")
        private BigDecimal servingSize;

        @JsonProperty("serving_unit")
        private String servingUnit;

        @JsonProperty("calories")
        private BigDecimal calories;

        @JsonProperty("protein_g")
        private BigDecimal proteinG;

        @JsonProperty("carbs_g")
        private BigDecimal carbsG;

        @JsonProperty("fat_g")
        private BigDecimal fatG;

        @JsonProperty("vitamin_c_mg")
        private BigDecimal vitaminCMg;

        @JsonProperty("calcium_mg")
        private BigDecimal calciumMg;

        @JsonProperty("fiber_g")
        private BigDecimal fiberG;

        @JsonProperty("vitamin_a_mcg")
        private BigDecimal vitaminAMcg;

        @JsonProperty("vitamin_b12_mcg")
        private BigDecimal vitaminB12Mcg;

        @JsonProperty("vitamin_d_mcg")
        private BigDecimal vitaminDMcg;

        @JsonProperty("iron_mg")
        private BigDecimal ironMg;

        @JsonProperty("potassium_mg")
        private BigDecimal potassiumMg;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @JsonProperty("status")
        private String status; // Trạng thái: "success" hoặc "ask_user"

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        @JsonProperty("question")
        private String question; // Câu hỏi AI muốn hỏi lại người dùng
        @JsonProperty("raw_input")

        private String rawInput; // THÊM BIẾN NÀY ĐỂ HỨNG DỮ LIỆU TỪ ANDROID
        public String getRawInput() { return rawInput; }
        public void setRawInput(String rawInput) { this.rawInput = rawInput; }
    }