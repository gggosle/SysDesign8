package com.sysdesign.banking.model;

public enum AccountType {
    checking,
    savings,
    credit;

    public AccountType fromValue(int value) {
        return AccountType.values()[value];
    }

    public int getValue() {
        return this.ordinal();
    }
}