package com.cintriq.agenda.core;

import javax.ejb.Local;

import com.cintriq.agenda.core.utilities.notification.EmailNotification;

/**
 * Interface for managing emailing services
 * 
 * @author kevint
 *
 */
@Local
public interface MailingService {
  
  boolean send(EmailNotification email);

}
