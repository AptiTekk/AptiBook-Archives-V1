package com.AptiTekk.Agenda.core.entity;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

import com.AptiTekk.Agenda.core.utilities.EqualsHelper;
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

        if (o == null) return false;

        if (!(o instanceof AppProperty)) return false;

        AppProperty other = (AppProperty) o;

        return EqualsHelper.areEquals(getKey(), other.getKey())
                && EqualsHelper.areEquals(getValue(), other.getValue());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getKey(), getValue());
    }
}
