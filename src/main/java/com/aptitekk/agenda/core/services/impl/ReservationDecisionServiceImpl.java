/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services.impl;

import com.aptitekk.agenda.core.entities.Reservation;
import com.aptitekk.agenda.core.entities.ReservationDecision;
import com.aptitekk.agenda.core.services.ReservationDecisionService;

import javax.ejb.Stateless;
import java.io.Serializable;

@Stateless
public class ReservationDecisionServiceImpl extends EntityServiceAbstract<ReservationDecision> implements ReservationDecisionService, Serializable {

    ReservationDecisionServiceImpl() {
        super(ReservationDecision.class);
    }

}
