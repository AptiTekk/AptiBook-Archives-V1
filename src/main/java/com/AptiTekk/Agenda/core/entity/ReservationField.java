package com.AptiTekk.Agenda.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservationField that = (ReservationField) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return 31 * (id + getClass().getName().hashCode());
    }
}
