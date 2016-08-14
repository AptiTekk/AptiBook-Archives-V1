/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.ReservationDecision;

import javax.ejb.Stateful;
import java.io.Serializable;

@Stateful
public class ReservationDecisionService extends MultiTenantEntityServiceAbstract<ReservationDecision> implements Serializable {

}