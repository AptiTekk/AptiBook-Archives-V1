package com.cintriq.agenda.core.impl;

import com.cintriq.agenda.core.TagService;
import com.cintriq.agenda.core.entity.QTag;
import com.cintriq.agenda.core.entity.Tag;

import javax.ejb.Stateless;

@Stateless
public class TagServiceImpl extends EntityServiceAbstract<Tag> implements TagService {

    private QTag tagTable = QTag.tag;

    public TagServiceImpl() {
        super(Tag.class);
    }
}
