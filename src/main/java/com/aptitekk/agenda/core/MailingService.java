package com.aptitekk.agenda.core;

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
