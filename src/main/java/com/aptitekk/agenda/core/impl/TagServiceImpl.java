package com.aptitekk.agenda.core.impl;

import com.aptitekk.agenda.core.TagService;
import com.aptitekk.agenda.core.entity.AssetType;
import com.aptitekk.agenda.core.entity.QTag;
import com.aptitekk.agenda.core.entity.Tag;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;

@Stateless
public class TagServiceImpl extends EntityServiceAbstract<Tag> implements TagService {

    private QTag tagTable = QTag.tag;

    public TagServiceImpl() {
        super(Tag.class);
    }

    @Override
    public Tag findByName(AssetType assetType, String name) {
        return new JPAQuery<Tag>(entityManager).from(tagTable).where(tagTable.name.eq(name).and(tagTable.assetType.eq(assetType))).fetchOne();
    }


}
