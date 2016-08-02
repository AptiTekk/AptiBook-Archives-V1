/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.entities.services;

import com.aptitekk.agenda.core.entities.ReservationField;
import com.aptitekk.agenda.core.entities.ReservationFieldEntry;

import javax.ejb.Stateful;
import java.io.Serializable;

@Stateful
public class ReservationFieldEntryService extends MultiTenantEntityServiceAbstract<ReservationFieldEntry> implements Serializable {
}
