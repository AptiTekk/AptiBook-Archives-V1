/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.woocommerce.restapi.objects;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {

    ACTIVE,
    PENDING_CANCEL,
    ON_HOLD,
    EXPIRED,
    CANCELLED;

    @JsonCreator
    public static Status create(String value) {
        switch (value.toLowerCase()) {
            case "active":
                return ACTIVE;
            case "pending-cancel":
                return PENDING_CANCEL;
            case "on-hold":
                return ON_HOLD;
            case "expired":
                return EXPIRED;
            case "cancelled":
                return CANCELLED;
            default:
                System.out.println("Unknown Status: " + value);
                return null;
        }
    }

}
