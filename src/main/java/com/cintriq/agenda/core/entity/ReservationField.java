package com.cintriq.agenda.core.entity;

import com.cintriq.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entity implementation class for Entity: ReservationField
 */
@Entity

public class ReservationField implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Column(length = 32)
    private String name;

    @Lob
    private String description;
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private AssetType assetType;

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

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
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
