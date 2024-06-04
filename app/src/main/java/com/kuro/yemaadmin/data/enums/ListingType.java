package com.kuro.yemaadmin.data.enums;

public enum ListingType {
    SALE("sale"),
    RENT("rent");

    public final String type;


    ListingType(String type) {
        this.type = type;
    }
}
