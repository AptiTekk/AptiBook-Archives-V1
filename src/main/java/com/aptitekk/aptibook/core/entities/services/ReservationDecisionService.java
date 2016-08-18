/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities.services;

import com.aptitekk.aptibook.core.entities.ReservationDecision;

import javax.ejb.Stateful;
import java.io.Serializable;

@Stateful
public class ReservationDecisionService extends MultiTenantEntityServiceAbstract<ReservationDecision> implements Serializable {

}
