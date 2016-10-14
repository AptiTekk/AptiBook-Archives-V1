/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class ReservationField extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String title;

    private String description;

    private boolean required;

    @ManyToOne
    private ResourceCategory resourceCategory;

    private boolean multiLine = false;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ReservationFieldEntry> reservationFieldEntries;

    public ReservationField() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return this.description;
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

    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    public void setResourceCategory(ResourceCategory resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservationField)) return false;

        ReservationField other = (ReservationField) o;

        return EqualsHelper.areEquals(getTitle(), other.getTitle())
                && EqualsHelper.areEquals(getDescription(), other.getDescription())
                && EqualsHelper.areEquals(isRequired(), other.isRequired())
                && EqualsHelper.areEquals(isMultiLine(), other.isMultiLine());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getTitle(), getDescription(), isRequired(), isMultiLine());
    }

    public List<ReservationFieldEntry> getReservationFieldEntries() {
        return reservationFieldEntries;
    }

    public void setReservationFieldEntries(List<ReservationFieldEntry> reservationFieldEntries) {
        this.reservationFieldEntries = reservationFieldEntries;
    }
}
