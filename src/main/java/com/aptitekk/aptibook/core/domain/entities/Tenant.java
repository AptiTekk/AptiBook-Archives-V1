/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.*;

@Entity
public class Tenant extends GlobalEntity {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false, unique = true)
    private int subscriptionId;

    @Column(nullable = false, unique = true, length = 32)
    private String slug;

    public int getId() {
        return id;
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

        return EqualsHelper.areEquals(getSubscriptionId(), other.getSubscriptionId())
                && EqualsHelper.areEquals(getSlug(), other.getSlug());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getSubscriptionId(), getSlug());
    }
}
