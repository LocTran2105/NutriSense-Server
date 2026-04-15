package hcmute.edu.vn.nitrisensebackend.dto;

import java.util.List;

public class DashboardDTO {
    private int avgCalories;
    private double avgWaterLiters;
    private double avgProtein;
    private double avgCarbs;
    private double avgFat;

    public double getAvgFiber() {
        return avgFiber;
    }

    public void setAvgFiber(double avgFiber) {
        this.avgFiber = avgFiber;
    }

    public double getAvgVitaminC() {
        return avgVitaminC;
    }

    public void setAvgVitaminC(double avgVitaminC) {
        this.avgVitaminC = avgVitaminC;
    }

    public double getAvgIron() {
        return avgIron;
    }

    public void setAvgIron(double avgIron) {
        this.avgIron = avgIron;
    }

    public double getAvgCalcium() {
        return avgCalcium;
    }

    public void setAvgCalcium(double avgCalcium) {
        this.avgCalcium = avgCalcium;
    }

    private double avgFiber;
    private double avgVitaminC;
    private double avgIron;
    private double avgCalcium;
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
    public List<ChartItemDTO> getChartData() { return chartData; }
    public void setChartData(List<ChartItemDTO> chartData) { this.chartData = chartData; }
}