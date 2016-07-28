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

    @Column(length = 32)
    private String name;

    @Lob
    private String description;
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private AssetCategory assetCategory;

    @Basic
    private Boolean largeField;

    public ReservationField() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
        this.assetCategory = assetCategory;
    }

    public Boolean getLargeField() {
        return largeField;
    }

    public void setLargeField(Boolean largeField) {
        this.largeField = largeField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservationField)) return false;

        ReservationField other = (ReservationField) o;

        return EqualsHelper.areEquals(getName(), other.getName())
                && EqualsHelper.areEquals(getDescription(), other.getDescription());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName(), getDescription());
    }
}
