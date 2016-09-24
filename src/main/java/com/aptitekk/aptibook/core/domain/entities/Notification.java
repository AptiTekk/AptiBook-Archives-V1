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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.ocpsoft.prettytime.PrettyTime;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class Notification extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private User user;

    private String subject;

    private String body;

    private DateTime creation = new DateTime(DateTimeZone.UTC);

    private Boolean notif_read = false;

    public Notification() {
        super();
    }

    public Notification(User user, String subject, String body) {
        setUser(user);
        setSubject(subject);
        setBody(body);
    }

    public String getTimeAgo() {
        PrettyTime prettyTime = new PrettyTime();
        return prettyTime.format(creation.toDate());
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

    public DateTime getCreation() {
        return creation;
    }

    public void setCreation(DateTime creation) {
        this.creation = creation;
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
