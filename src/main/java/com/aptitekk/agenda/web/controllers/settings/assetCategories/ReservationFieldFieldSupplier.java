/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.web.controllers.settings.assetCategories;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ReservationFieldFieldSupplier {

    /**
     * Title of the Reservation Field.
     */
    @Size(max = 64, message = "This may only be 64 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String title;

    /**
     * Description of the Reservation Field.
     */
    @Size(max = 256, message = "This may only be 256 characters long.")
    @Pattern(regexp = "[^<>;=]*", message = "These characters are not allowed: < > ; =")
    String description;

    /**
     * Values for the size selection combo box.
     */
    final String[] sizes = {"Single-Line", "Multi-Line"};

    /**
     * Chosen size from the combo box.
     */
    String size;

}
