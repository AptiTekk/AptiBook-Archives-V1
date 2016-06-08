package com.cintriq.agenda.core.entity;

import com.cintriq.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entity implementation class for Entity: AssetType
 */
@Entity
public class AssetType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
    private List<Asset> assets = new ArrayList<>();

    @OneToMany(mappedBy = "assetType", cascade = CascadeType.REMOVE)
    @OrderColumn(name = "`Order`")
    private List<ReservationField> reservationFields = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "assetType")
    private List<Tag> tags = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public AssetType() {
        super();
    }

    public AssetType(String name) {
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

        if (!(o instanceof AssetType)) return false;

        AssetType other = (AssetType) o;

        return EqualsHelper.areEquals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName());
    }
}
