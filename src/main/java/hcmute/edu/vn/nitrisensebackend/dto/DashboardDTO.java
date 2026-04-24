package hcmute.edu.vn.nitrisensebackend.dto;

import java.util.List;

public class DashboardDTO {

    private int avgCalories;
    private double avgWaterLiters;
    private double avgProtein;
    private double avgCarbs;
    private double avgFat;
    private double avgFiber;
    private double avgVitaminC;
    private double avgIron;
    private double avgCalcium;

    private int totalCalories;
    private int targetCalories;
    private double totalWaterLiters;
    private double targetWaterLiters;
    private double totalProtein;
    private double targetProtein;
    private double totalCarbs;
    private double targetCarbs;
    private double totalFat;
    private double targetFat;
    private double totalFiber;
    private double targetFiber;
    private double totalVitaminC;
    private double targetVitaminC;
    private double totalIron;
    private double targetIron;
    private double totalCalcium;
    private double targetCalcium;

    private List<ChartItemDTO> chartData;

    public static class ChartItemDTO {
        private String date;
        private int calories;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public int getCalories() { return calories; }
        public void setCalories(int calories) { this.calories = calories; }
    }

    public int getAvgCalories() { return avgCalories; }
    public void setAvgCalories(int avgCalories) { this.avgCalories = avgCalories; }
    public double getAvgWaterLiters() { return avgWaterLiters; }
    public void setAvgWaterLiters(double avgWaterLiters) { this.avgWaterLiters = avgWaterLiters; }
    public double getAvgProtein() { return avgProtein; }
    public void setAvgProtein(double avgProtein) { this.avgProtein = avgProtein; }
    public double getAvgCarbs() { return avgCarbs; }
    public void setAvgCarbs(double avgCarbs) { this.avgCarbs = avgCarbs; }
    public double getAvgFat() { return avgFat; }
    public void setAvgFat(double avgFat) { this.avgFat = avgFat; }
    public double getAvgFiber() { return avgFiber; }
    public void setAvgFiber(double avgFiber) { this.avgFiber = avgFiber; }
    public double getAvgVitaminC() { return avgVitaminC; }
    public void setAvgVitaminC(double avgVitaminC) { this.avgVitaminC = avgVitaminC; }
    public double getAvgIron() { return avgIron; }
    public void setAvgIron(double avgIron) { this.avgIron = avgIron; }
    public double getAvgCalcium() { return avgCalcium; }
    public void setAvgCalcium(double avgCalcium) { this.avgCalcium = avgCalcium; }
    public List<ChartItemDTO> getChartData() { return chartData; }
    public void setChartData(List<ChartItemDTO> chartData) { this.chartData = chartData; }
    public int getTotalCalories() { return totalCalories; }
    public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }
    public int getTargetCalories() { return targetCalories; }
    public void setTargetCalories(int targetCalories) { this.targetCalories = targetCalories; }

    public double getTotalWaterLiters() { return totalWaterLiters; }
    public void setTotalWaterLiters(double totalWaterLiters) { this.totalWaterLiters = totalWaterLiters; }
    public double getTargetWaterLiters() { return targetWaterLiters; }
    public void setTargetWaterLiters(double targetWaterLiters) { this.targetWaterLiters = targetWaterLiters; }

    public double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(double totalProtein) { this.totalProtein = totalProtein; }
    public double getTargetProtein() { return targetProtein; }
    public void setTargetProtein(double targetProtein) { this.targetProtein = targetProtein; }

    public double getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(double totalCarbs) { this.totalCarbs = totalCarbs; }
    public double getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(double targetCarbs) { this.targetCarbs = targetCarbs; }

    public double getTotalFat() { return totalFat; }
    public void setTotalFat(double totalFat) { this.totalFat = totalFat; }
    public double getTargetFat() { return targetFat; }
    public void setTargetFat(double targetFat) { this.targetFat = targetFat; }

    public double getTotalFiber() { return totalFiber; }
    public void setTotalFiber(double totalFiber) { this.totalFiber = totalFiber; }
    public double getTargetFiber() { return targetFiber; }
    public void setTargetFiber(double targetFiber) { this.targetFiber = targetFiber; }

    public double getTotalVitaminC() { return totalVitaminC; }
    public void setTotalVitaminC(double totalVitaminC) { this.totalVitaminC = totalVitaminC; }
    public double getTargetVitaminC() { return targetVitaminC; }
    public void setTargetVitaminC(double targetVitaminC) { this.targetVitaminC = targetVitaminC; }

    public double getTotalIron() { return totalIron; }
    public void setTotalIron(double totalIron) { this.totalIron = totalIron; }
    public double getTargetIron() { return targetIron; }
    public void setTargetIron(double targetIron) { this.targetIron = targetIron; }

    public double getTotalCalcium() { return totalCalcium; }
    public void setTotalCalcium(double totalCalcium) { this.totalCalcium = totalCalcium; }
    public double getTargetCalcium() { return targetCalcium; }
    public void setTargetCalcium(double targetCalcium) { this.targetCalcium = targetCalcium; }
}