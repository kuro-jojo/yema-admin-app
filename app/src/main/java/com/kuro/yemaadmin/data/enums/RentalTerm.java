package com.kuro.yemaadmin.data.enums;

public enum RentalTerm {
    MONTH("month"),
    YEAR("year");

    public final String term;

    RentalTerm(String term) {
        this.term = term;
    }
}