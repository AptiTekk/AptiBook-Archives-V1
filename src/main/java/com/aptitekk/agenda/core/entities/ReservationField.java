/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.entities.util.MultiTenantEntity;
import com.aptitekk.agenda.core.util.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ReservationField extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String title;

    private String description;

    private boolean required;

    @ManyToOne
    private AssetCategory assetCategory;

    private boolean multiLine = false;

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

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
        this.assetCategory = assetCategory;
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
}
