/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.services;

import com.aptitekk.agenda.core.utilities.notification.EmailNotification;

import javax.ejb.Local;

/**
 * Interface for managing emailing services
 *
 * @author kevint
 */
@Local
public interface MailingService {

    boolean send(EmailNotification email);

}
