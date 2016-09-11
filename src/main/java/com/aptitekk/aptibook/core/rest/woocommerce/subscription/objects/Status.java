/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.rest.woocommerce.subscription.objects;

import com.google.gson.annotations.SerializedName;

public enum Status {

    @SerializedName("active")
    ACTIVE,
    @SerializedName("pending-cancel")
    PENDING_CANCEL,
    @SerializedName("on-hold")
    ON_HOLD,
    @SerializedName("expired")
    EXPIRED,
    @SerializedName("cancelled")
    CANCELLED;

}
