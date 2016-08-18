/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities;

import com.aptitekk.aptibook.core.entities.util.MultiTenantEntity;
import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tag extends MultiTenantEntity implements Comparable<Tag> {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AssetCategory assetCategory;

    @ManyToMany(mappedBy = "tags")
    private List<Asset> assets = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int idTag) {
        this.id = idTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetCategory getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
        this.assetCategory = assetCategory;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Tag)) return false;

        Tag other = (Tag) o;

        return EqualsHelper.areEquals(getName(), other.getName()) && EqualsHelper.areEquals(getAssetCategory(), other.getAssetCategory());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName(), getAssetCategory());
    }

    @Override
    public int compareTo(Tag o) {
        return name.compareTo(o.getName());
    }
}
