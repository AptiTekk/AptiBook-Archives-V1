/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ResourceCategory extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany(mappedBy = "resourceCategory", cascade = CascadeType.REMOVE)
    @OrderBy(value = "name")
    private List<Resource> resources = new ArrayList<>();

    @OneToMany(mappedBy = "resourceCategory", cascade = CascadeType.REMOVE)
    private List<ReservationField> reservationFields = new ArrayList<>();

    @OneToMany(mappedBy = "resourceCategory", cascade = CascadeType.REMOVE)
    @OrderBy(value = "name")
    private List<Tag> tags = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public ResourceCategory() {
        super();
    }

    public ResourceCategory(String name) {
        super();
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
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

        if (!(o instanceof ResourceCategory)) return false;

        ResourceCategory other = (ResourceCategory) o;

        return EqualsHelper.areEquals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getName());
    }
}
