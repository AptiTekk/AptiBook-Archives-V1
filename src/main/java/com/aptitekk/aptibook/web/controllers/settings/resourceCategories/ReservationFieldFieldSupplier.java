/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.web.controllers.settings.resourceCategories;

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
     * Whether or not the field is required.
     */
    boolean required;

    /**
     * Values for the size selection combo box.
     */
    final String[] sizes = {"Single-Line", "Multi-Line"};

    /**
     * Chosen size from the combo box.
     */
    String size;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String[] getSizes() {
        return sizes;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
