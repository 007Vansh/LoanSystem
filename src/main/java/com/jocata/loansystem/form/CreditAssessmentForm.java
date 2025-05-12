package com.jocata.loansystem.form;

public class CreditAssessmentForm {
    private String income;
    private String panNo;
    private String requestedAmount;
    private String requestedTenure;

    public String getRequestedTenure() {
        return requestedTenure;
    }

    public void setRequestedTenure(String requestedTenure) {
        this.requestedTenure = requestedTenure;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(String requestedAmount) {
        this.requestedAmount = requestedAmount;
    }
}
