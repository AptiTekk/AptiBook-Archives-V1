/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.entities.superClasses.MultiTenantEntity;
import com.aptitekk.agenda.core.utilities.EqualsHelper;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ReservationFieldEntry extends MultiTenantEntity implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private Reservation reservation;

    @ManyToOne
    private ReservationField field;

    @Lob
    private String content;

    private static final long serialVersionUID = 1L;

    public ReservationFieldEntry() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ReservationField getField() {
        return this.field;
    }

    public void setField(ReservationField field) {
        this.field = field;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof ReservationFieldEntry)) return false;

        ReservationFieldEntry other = (ReservationFieldEntry) o;

        return EqualsHelper.areEquals(getContent(), other.getContent());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getContent());
    }
}
