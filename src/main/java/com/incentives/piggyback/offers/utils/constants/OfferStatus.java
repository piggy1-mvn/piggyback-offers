package com.incentives.piggyback.offers.utils.constants;

import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public enum OfferStatus {
    ACTIVE,
    INACTIVE,
    ARCHIVED;

    public static ArrayList<String> getAllStatus() {
        OfferStatus[] status = OfferStatus.values();
        ArrayList<String> stringStatus = new ArrayList<>();
        for (OfferStatus role : status) {
            stringStatus.add(role.toString());
        }
        return stringStatus;
    }
}
