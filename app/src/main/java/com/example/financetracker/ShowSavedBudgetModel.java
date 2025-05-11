package com.example.financetracker;

public class ShowSavedBudgetModel {
    private String startDate;
    private String endDate;
    private String totalBudget;
    private String budgetUID;

    public ShowSavedBudgetModel() {
    }

    public ShowSavedBudgetModel(String startDate, String endDate, String totalBudget, String budgetUID) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
        this.budgetUID = budgetUID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(String totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getBudgetUID() {
        return budgetUID;
    }

    public void setBudgetUID(String budgetUID) {
        this.budgetUID = budgetUID;
    }
}
