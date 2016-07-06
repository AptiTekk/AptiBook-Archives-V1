/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.services.TagService;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.QTag;
import com.aptitekk.agenda.core.entity.Tag;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;
import java.io.Serializable;

@Stateless
public class TagServiceImpl extends EntityServiceAbstract<Tag> implements TagService, Serializable {

    private QTag tagTable = QTag.tag;

    public TagServiceImpl() {
        super(Tag.class);
    }

    @Override
    public Tag findByName(AssetType assetType, String name) {
        return new JPAQuery<Tag>(entityManager).from(tagTable).where(tagTable.name.eq(name).and(tagTable.assetType.eq(assetType))).fetchOne();
    }


}
