/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.entities;


import com.aptitekk.aptibook.core.entities.util.MultiTenantEntity;
import com.aptitekk.aptibook.core.util.EqualsHelper;
import com.aptitekk.aptibook.core.util.LogManager;
import com.aptitekk.aptibook.core.util.propertyTypes.BooleanField;
import com.aptitekk.aptibook.core.util.propertyTypes.SingleLineField;
import com.aptitekk.aptibook.core.util.propertyTypes.abstractTypes.PropertyType;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Property extends MultiTenantEntity implements Serializable {

    public enum Group {

        GOOGLE_SIGN_IN("Google Sign In", null);

        private String friendlyName;
        private Class<? extends ChangeListener> propertyGroupChangeListenerClass;

        Group(String friendlyName, Class<? extends ChangeListener> propertyGroupChangeListenerClass) {
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
                    Set<Bean<?>> beanSet = beanManager.getBeans(ChangeListener.class);
                    Bean<?> bean = null;
                    for (Bean<?> beanInSet : beanSet) {
                        if (beanInSet.getBeanClass().equals(propertyGroupChangeListenerClass)) {
                            bean = beanInSet;
                            break;
                        }
                    }

                    if (bean != null) {
                        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
                        ChangeListener listener = (ChangeListener) beanManager.getReference(bean, ChangeListener.class, creationalContext);
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

        public interface ChangeListener {
            void onPropertiesChanged(Group propertyGroup);
        }
    }

    public enum Key {

        GOOGLE_SIGN_IN_ENABLED("false", Group.GOOGLE_SIGN_IN, new BooleanField("Enable Google Sign In")),
        GOOGLE_SIGN_IN_WHITELIST("gmail.com, example.com", Group.GOOGLE_SIGN_IN, new SingleLineField("Allowed Domain Names (Comma separated)", 256));

        private final String defaultValue;
        private final Group group;
        private PropertyType propertyType;

        Key(String defaultValue, Group group, PropertyType propertyType) {
            this.defaultValue = defaultValue;
            this.group = group;
            this.propertyType = propertyType;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public Group getGroup() {
            return group;
        }

        public PropertyType getPropertyType() {
            return propertyType;
        }
    }

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private Key propertyKey;

    private String propertyValue;

    private static final long serialVersionUID = 1L;

    public Property() {
    }

    public Property(Key propertyKey, String propertyValue) {
        setPropertyKey(propertyKey);
        setPropertyValue(propertyValue);
    }

    public Key getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(Key key) {
        this.propertyKey = key;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public void setPropertyValue(String value) {
        this.propertyValue = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) return false;

        if (!(o instanceof Property)) return false;

        Property other = (Property) o;

        return EqualsHelper.areEquals(getPropertyKey(), other.getPropertyKey())
                && EqualsHelper.areEquals(getPropertyValue(), other.getPropertyValue());
    }

    @Override
    public int hashCode() {
        return EqualsHelper.calculateHashCode(getPropertyKey(), getPropertyValue());
    }
}
