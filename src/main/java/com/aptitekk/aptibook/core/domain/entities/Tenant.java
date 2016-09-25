/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.util.List;

@Entity
public class Tenant extends GlobalEntity {

    @Id
    @GeneratedValue
    private int id;

    private boolean active;

    private DateTime timeSetInactive;

    @Column(nullable = false, unique = true)
    private int subscriptionId;

    @Column(nullable = false, unique = true)
    private String slug;

    // ----------------------------------------------------------- Tenant Dependent Entities

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<Asset> assets;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<AssetCategory> assetCategories;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<File> files;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<Permission> permissions;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<Property> properties;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<ReservationDecision> reservationDecisions;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<ReservationField> reservationFields;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<ReservationFieldEntry> reservationFieldEntries;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<Tag> tags;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<User> users;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<UserGroup> userGroups;

    // ----------------------------------------------------------- End Tenant Dependent Entities

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active)
            timeSetInactive = null;
        else
            timeSetInactive = new DateTime(DateTimeZone.UTC);
    }

    /**
     * This is the time when the Tenant active value was set to false. (In UTC).
     *
     * @return The time when the Tenant was set inactive if it is inactive, or null if it was active. The time will be in UTC.
     */
    public DateTime getTimeSetInactive() {
        return timeSetInactive;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Tenant)) return false;

        Tenant other = (Tenant) o;

        return EqualsHelper.areEquals(isActive(), other.isActive()) && EqualsHelper.areEquals(getTimeSetInactive(), other.getTimeSetInactive()) && EqualsHelper.areEquals(getSubscriptionId(), other.getSubscriptionId())
                && EqualsHelper.areEquals(getSlug(), other.getSlug());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(isActive(), getTimeSetInactive(), getSubscriptionId(), getSlug());
    }
}
