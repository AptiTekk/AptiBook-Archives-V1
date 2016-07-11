/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.utilities.notification;

import com.aptitekk.agenda.core.entities.Notification;

/**
 * Created by kevint on 5/13/2016.
 */
public interface NotificationListener {

    void pushNotification(Notification n);

}
