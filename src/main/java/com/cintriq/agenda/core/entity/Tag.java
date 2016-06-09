package com.cintriq.agenda.core.entity;

import com.cintriq.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tag implements Comparable<Tag> {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AssetType assetType;

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

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
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

        return EqualsHelper.areEquals(getName(), other.getName()) && EqualsHelper.areEquals(getAssetType(), other.getAssetType());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName(), getAssetType());
    }

    @Override
    public int compareTo(Tag o) {
        return name.compareTo(o.getName());
    }
}
