/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities;

import com.aptitekk.agenda.core.entities.util.MultiTenantEntity;
import com.aptitekk.agenda.core.util.EqualsHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class File extends MultiTenantEntity {

    @Id
    @GeneratedValue
    private int id;

    private byte[] data;

    public int getId() {
        return id;
    }

    public void setId(int idTag) {
        this.id = idTag;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof File)) return false;

        File other = (File) o;

        return EqualsHelper.areEquals(getData(), other.getData());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(data);
    }
}
