/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tenant {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false, unique = true)
    private int subscriptionId;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Asset> assets = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<AssetType> assetTypes = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Permission> permissions = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Property> properties = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<ReservationDecision> reservationDecisions = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<ReservationField> reservationFields = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<ReservationFieldEntry> reservationFieldEntries = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.REMOVE)
    private List<UserGroup> userGroups = new ArrayList<>();

    public int getId() {
        return id;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Tenant)) return false;

        Tenant other = (Tenant) o;

        return EqualsHelper.areEquals(getSubscriptionId(), other.getSubscriptionId());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getSubscriptionId());
    }

}
