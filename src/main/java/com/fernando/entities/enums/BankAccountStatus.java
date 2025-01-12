package com.fernando.entities.enums;

public enum BankAccountStatus {

    OPENED("OPENED"),
    CLOSED("CLOSED");

    private String status;

    BankAccountStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
