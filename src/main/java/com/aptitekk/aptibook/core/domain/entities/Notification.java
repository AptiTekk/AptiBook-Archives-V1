/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aptitekk.aptibook.core.domain.entities;

import com.aptitekk.aptibook.core.util.EqualsHelper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.ZonedDateTime;


@Entity
public class Notification extends MultiTenantEntity implements Serializable {

    public enum Type {

        TYPE_RESERVATION_APPROVED("Email when your Reservation Request is Approved", true, false, null),
        TYPE_RESERVATION_REJECTED("Email when your Reservation Request is Rejected", true, false, null),
        TYPE_RESERVATION_REQUESTED("Email when a Reservation is Requested", true, true, null),
        TYPE_RESERVATION_REQUEST_AUTO_APPROVED("Email upon Automatic Approval of a Reservation Request", false, true, null),
        TYPE_APPROVAL_REQUEST("Email when a New User Registers", true, true, Permission.Descriptor.USERS_MODIFY_ALL);

        private final String label;
        private final boolean defaultValue;
        private final boolean userGroupRequired;
        private final Permission.Descriptor requiredPermissionDescriptor;

        Type(String label, boolean defaultValue, boolean userGroupRequired, Permission.Descriptor requiredPermissionDescriptor) {
            this.label = label;
            this.defaultValue = defaultValue;
            this.userGroupRequired = userGroupRequired;
            this.requiredPermissionDescriptor = requiredPermissionDescriptor;
        }

        public String getLabel() {
            return label;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }

        public boolean isUserGroupRequired() {
            return userGroupRequired;
        }

        public Permission.Descriptor getRequiredPermissionDescriptor() {
            return requiredPermissionDescriptor;
        }
    }

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private User user;

    private String subject;

    private String body;

    private ZonedDateTime creation = ZonedDateTime.now();

    private Boolean notif_read = false;

    public Notification() {
        super();
    }

    public Notification(User user, String subject, String body) {
        setUser(user);
        setSubject(subject);
        setBody(body);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getCreation() {
        return creation;
    }

    public Boolean getRead() {
        return notif_read;
    }

    public void setRead(Boolean notif_read) {
        this.notif_read = notif_read;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Notification)) return false;

        Notification other = (Notification) o;

        return EqualsHelper.areEquals(getSubject(), other.getSubject())
                && EqualsHelper.areEquals(getBody(), other.getBody())
                && EqualsHelper.areEquals(getCreation(), other.getCreation())
                && EqualsHelper.areEquals(getRead(), other.getRead());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getSubject(), getBody(), getCreation(), getRead());
    }

}
