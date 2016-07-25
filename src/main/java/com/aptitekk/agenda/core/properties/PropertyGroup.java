/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of Agenda, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.agenda.core.properties;

import com.aptitekk.agenda.core.util.LogManager;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import java.util.Set;

public enum PropertyGroup {

    FRONT_PAGE("Front Page", null);

    private String friendlyName;
    private Class<? extends PropertyGroupChangeListener> propertyGroupChangeListenerClass;

    PropertyGroup(String friendlyName, Class<? extends PropertyGroupChangeListener> propertyGroupChangeListenerClass) {
        this.friendlyName = friendlyName;
        this.propertyGroupChangeListenerClass = propertyGroupChangeListenerClass;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void firePropertiesChangedEvent() {
        if (propertyGroupChangeListenerClass != null) {
            try {
                BeanManager beanManager = CDI.current().getBeanManager();
                Set<Bean<?>> beanSet = beanManager.getBeans(PropertyGroupChangeListener.class);
                Bean<?> bean = null;
                for (Bean<?> beanInSet : beanSet) {
                    if (beanInSet.getBeanClass().equals(propertyGroupChangeListenerClass)) {
                        bean = beanInSet;
                        break;
                    }
                }

                if (bean != null) {
                    CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
                    PropertyGroupChangeListener listener = (PropertyGroupChangeListener) beanManager.getReference(bean, PropertyGroupChangeListener.class, creationalContext);
                    if (listener != null)
                        listener.onPropertiesChanged(this);
                } else {
                    LogManager.logError("Tried to access listener for " + propertyGroupChangeListenerClass.getName() + " but could not find it in the bean set.");
                }
            } catch (UnsatisfiedResolutionException e) {
                LogManager.logError("Tried to access listener for " + propertyGroupChangeListenerClass.getName() + " but could not find it." +
                        "\nError: " + e.getMessage());
            }
        }
    }
}
