package com.AptiTekk.Agenda.core.entity;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * Entity implementation class for Entity: Property
 */
@Entity
@NamedQuery(name = "AppProperty.findAll", query = "SELECT e FROM AppProperty e")
public class AppProperty implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String propertyKey;
    private String propertyValue;

    private static final long serialVersionUID = 1L;

    public AppProperty() {

    }

    public AppProperty(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public String getKey() {
        return this.propertyKey;
    }

    public void setKey(String key) {
        this.propertyKey = key;
    }

    public String getValue() {
        return this.propertyValue;
    }

    public void setValue(String value) {
        this.propertyValue = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppProperty that = (AppProperty) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return 31 * (id + getClass().getName().hashCode());
    }
}
