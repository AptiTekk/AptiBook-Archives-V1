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
import java.util.ArrayList;
import java.util.List;

@Entity
public class AssetCategory extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany(mappedBy = "assetCategory", cascade = CascadeType.REMOVE)
    @OrderBy(value = "name")
    private List<Asset> assets = new ArrayList<>();

    @OneToMany(mappedBy = "assetCategory", cascade = CascadeType.REMOVE)
    private List<ReservationField> reservationFields = new ArrayList<>();

    @OneToMany(mappedBy = "assetCategory", cascade = CascadeType.REMOVE)
    @OrderBy(value = "name")
    private List<Tag> tags = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public AssetCategory() {
        super();
    }

    public AssetCategory(String name) {
        super();
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReservationField> getReservationFields() {
        return reservationFields;
    }

    public void setReservationFields(List<ReservationField> reservationFields) {
        this.reservationFields = reservationFields;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof AssetCategory)) return false;

        AssetCategory other = (AssetCategory) o;

        return EqualsHelper.areEquals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName());
    }
}
