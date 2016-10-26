/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
public class Tenant extends GlobalEntity {

    @Id
    @GeneratedValue
    private int id;

    private boolean active;

    private String adminEmail;

    private ZonedDateTime timeSetInactive;

    @Column(nullable = false, unique = true)
    private int subscriptionId;

    @Column(nullable = false, unique = true)
    private String slug;


    public enum Tier {
        BRONZE("aptibook-bronze", 25, 5, 50),
        SILVER("aptibook-silver", 100, 10, 100),
        PLATINUM("aptibook-platinum", -1, -1, 200);

        private String sku;
        private final int allowedResources;
        private final int allowedResourceCategories;
        private final int allowedUsers;

        Tier(String sku, int allowedResources, int allowedResourceCategories, int allowedUsers) {
            this.sku = sku;
            this.allowedResources = allowedResources;
            this.allowedResourceCategories = allowedResourceCategories;
            this.allowedUsers = allowedUsers;
        }

        public String getSku() {
            return sku;
        }

        /**
         * Returns the number of Allowed Resources for this Tier.
         * A value of -1 means "Unlimited."
         */
        public int getAllowedResources() {
            return allowedResources;
        }

        /**
         * Returns the number of Allowed Resource Categories for this Tier.
         * A value of -1 means "Unlimited."
         */
        public int getAllowedResourceCategories() {
            return allowedResourceCategories;
        }

        /**
         * Returns the number of Allowed Users for this Tier.
         * A value of -1 means "Unlimited."
         */
        public int getAllowedUsers() {
            return allowedUsers;
        }

        public static Tier getTierBySku(String sku) {
            for(Tier tier : values()) {
                if(tier.getSku().equals(sku))
                    return tier;
            }

            return null;
        }
    }

    @Enumerated(EnumType.STRING)
    private Tier tier;

    // ----------------------------------------------------------- Tenant Dependent Entities

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<Resource> resources;

    @OneToMany(mappedBy = "tenant", cascade = javax.persistence.CascadeType.REMOVE)
    private List<ResourceCategory> resourceCategories;

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
            timeSetInactive = ZonedDateTime.now();
    }

    /**
     * This is the time when the Tenant active value was set to false. (In UTC).
     *
     * @return The time when the Tenant was set inactive if it is inactive, or null if it was active. The time will be in UTC.
     */
    public ZonedDateTime getTimeSetInactive() {
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

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Tenant)) return false;

        Tenant other = (Tenant) o;

        return EqualsHelper.areEquals(isActive(), other.isActive()) && EqualsHelper.areEquals(getTimeSetInactive(), other.getTimeSetInactive()) && EqualsHelper.areEquals(getSubscriptionId(), other.getSubscriptionId())
                && EqualsHelper.areEquals(getSlug(), other.getSlug()) && EqualsHelper.areEquals(getTier(), other.getTier());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(isActive(), getTimeSetInactive(), getSubscriptionId(), getSlug(), getTier());
    }
}
