package hcmute.edu.vn.nitrisensebackend.dto;

import hcmute.edu.vn.nitrisensebackend.entity.FoodItem;

import java.util.List;

public class AiAnalyzeResult {

    private boolean askUser;
    private String question;
    private List<FoodItem> foodItems;
    public AiAnalyzeResult() {}


    public boolean isAskUser() {
        return askUser;
    }

    public void setAskUser(boolean askUser) {
        this.askUser = askUser;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<FoodItem> getFoodItems() { return foodItems; }
    public void setFoodItems(List<FoodItem> foodItems) { this.foodItems = foodItems; }
}