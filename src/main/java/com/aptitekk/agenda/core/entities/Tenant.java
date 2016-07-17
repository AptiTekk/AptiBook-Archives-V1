/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;

@Entity
@Table(schema = "MASTER")
@NamedQueries({@NamedQuery(name = "Tenant.getBySubscriptionId", query = "SELECT t FROM Tenant t WHERE t.subscriptionId = :subscriptionId")})
public class Tenant {

    @Id
    @GeneratedValue
    private int id;

    private int subscriptionId;

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
