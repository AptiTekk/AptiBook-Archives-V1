package com.cintriq.agenda.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Pasha on 6/7/2016.
 */
@Entity
public class Tag {
    private int idTag;
    private String tag;

    @Id
    @Column(name = "idTag")
    public int getIdTag() {
        return idTag;
    }

    public void setIdTag(int idTag) {
        this.idTag = idTag;
    }

    @Basic
    @Column(name = "Tag")
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag1 = (Tag) o;

        if (idTag != tag1.idTag) return false;
        if (tag != null ? !tag.equals(tag1.tag) : tag1.tag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idTag;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
}
