/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

import java.util.List;
import java.util.StringJoiner;

public class CustomNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {

    @Override
    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
        List<Identifier> columnNames = source.getColumnNames();
        StringJoiner stringJoiner = new StringJoiner("_");
        for (Identifier identifier : columnNames) {
            stringJoiner.add(identifier.getCanonicalName());
        }
        return toIdentifier("FK_" + stringJoiner.toString(), source.getBuildingContext());
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        return toIdentifier(super.determineJoinTableName(source).getText().toLowerCase(), source.getBuildingContext());
    }
}
