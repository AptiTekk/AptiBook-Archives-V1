/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.cron;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.rest.woocommerce.subscription.objects.*;
import com.aptitekk.aptibook.core.rest.woocommerce.util.WooCommerceSecurityFilter;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.util.LogManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.threeten.extra.Days;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TenantSynchronizer {

    private static final String WOOCOMMERCE_URL = System.getenv("WOOCOMMERCE_URL");
    private static final String WOOCOMMERCE_CK = System.getenv("WOOCOMMERCE_CK");
    private static final String WOOCOMMERCE_CS = System.getenv("WOOCOMMERCE_CS");

    private static final int APTIBOOK_SKU = 1;
    private static final String URL_SLUG_META_KEY = "URL Slug";

    @Inject
    private TenantService tenantService;

    @Inject
    private TenantManagementService tenantManagementService;

    @Schedule(minute = "*", hour = "*", persistent = false)
    public void synchronizeTenants() {
        LogManager.logDebug(getClass(), "Synchronizing Tenants...");

        if (WOOCOMMERCE_URL == null || WOOCOMMERCE_CK == null || WOOCOMMERCE_CS == null) {
            LogManager.logError(getClass(), "Failed to Synchronize due to missing environment variable(s).");
            return;
        }

        List<Subscription> subscriptions = getSubscriptions();
        if (subscriptions != null) {

            /*
              Contains a list of the encountered subscriptions containing AptiBook, by ID.
              Used for disabling tenants which have had their subscription removed from WooCommerce.
             */
            List<Integer> subscriptionIdsEncountered = new ArrayList<>();

            for (Subscription subscription : subscriptions) {
                //Check for all the AptiBook instances within this subscription
                List<LineItem> lineItems = subscription.getLineItems();
                if (lineItems != null && !lineItems.isEmpty()) {
                    for (LineItem lineItem : lineItems) {

                        //Check that the line item is AptiBook
                        if (lineItem.getSku() == APTIBOOK_SKU) {
                            subscriptionIdsEncountered.add(subscription.getId());

                            Tenant currentTenant = tenantService.getTenantBySubscriptionId(subscription.getId());

                            //Change Tenant slug if needed.
                            String slug = getSlugFromLineItem(lineItem);
                            if (slug == null || slug.isEmpty()) {
                                if (currentTenant != null) {
                                    changeTenantActive(currentTenant, false);
                                }
                            } else if (currentTenant != null) {
                                if (!currentTenant.getSlug().equalsIgnoreCase(slug)) {
                                    changeTenantSlug(currentTenant, slug);
                                }
                            }

                            //Set Tenant Active or Inactive based on its subscription status.
                            Status status = subscription.getStatus();
                            if (currentTenant == null) {
                                if (status == Status.ACTIVE) {
                                    createNewTenant(subscription.getId(), slug);
                                    //TODO Send email to tenant billing_address
                                }
                            } else {
                                if (status != Status.ACTIVE) {
                                    changeTenantActive(currentTenant, false);
                                } else {
                                    changeTenantActive(currentTenant, true);
                                }
                            }

                            //Delete tenant if it has been inactive for 30 or more days.
                            if (currentTenant != null && !currentTenant.isActive()) {
                                ZonedDateTime timeSetInactive = currentTenant.getTimeSetInactive();
                                if (timeSetInactive != null) {
                                    if (Days.between(timeSetInactive, ZonedDateTime.now()).getAmount() > 30)
                                        deleteTenant(currentTenant);
                                }
                            }

                            break;
                        }
                    }
                }
            }

            for (Tenant tenant : tenantService.getAll()) {
                if (subscriptionIdsEncountered.contains(tenant.getSubscriptionId()))
                    continue;

                changeTenantActive(tenant, false);
            }
        }

        tenantManagementService.refresh();
        LogManager.logDebug(getClass(), "Synchronization Complete.");
    }

    /**
     * Contacts WooCommerce and retrieves a list of Subscriptions.
     *
     * @return The list of Subscriptions.
     */
    private List<Subscription> getSubscriptions() {
        URLConnectionEngine urlConnectionEngine = new URLConnectionEngine();

        ResteasyClientBuilder builder = new ResteasyClientBuilder();
        builder.register(new WooCommerceSecurityFilter(
                WOOCOMMERCE_CK,
                WOOCOMMERCE_CS));
        builder.httpEngine(urlConnectionEngine);
        ResteasyClient webClient = builder.build();
        ResteasyWebTarget webTarget = webClient.target(WOOCOMMERCE_URL);
        SubscriptionService service = webTarget.proxy(SubscriptionService.class);
        try {
            return service.getAll().getSubscriptions();
        } catch (ClientErrorException e) {
            LogManager.logException(getClass(), "Could not Synchronize Tenants due to Client Error", e);
            return null;
        } catch (Exception e) {
            LogManager.logException(getClass(), "Could not Synchronize Tenants due to Unknown Error.", e);
            return null;
        }
    }

    /**
     * Retrieves the slug from the provided line item, if it exists.
     *
     * @param lineItem The line item to get the slug from.
     * @return The slug if one exists, null otherwise.
     */
    private String getSlugFromLineItem(LineItem lineItem) {
        List<MetaItem> meta = lineItem.getMeta();
        if (meta != null && !meta.isEmpty()) {
            for (MetaItem metaItem : meta) {
                if (metaItem.getKey().equalsIgnoreCase(URL_SLUG_META_KEY)) {
                    return metaItem.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Changes the provided tenant's slug to the specified slug, unless another tenant with that slug exists.
     *
     * @param tenant  The tenant whose slug should be changed.
     * @param newSlug The new slug.
     */
    private void changeTenantSlug(Tenant tenant, String newSlug) {
        if (tenant.getSlug().equalsIgnoreCase(newSlug))
            return;

        String previousSlug = tenant.getSlug();
        if (tenantService.getTenantBySlug(newSlug) == null) {
            tenant.setSlug(newSlug);
            try {
                tenant = tenantService.merge(tenant);
                LogManager.logInfo(getClass(), "Updated Slug For Tenant ID " + tenant.getId() + ". Previously: " + previousSlug + "; Now: " + newSlug);
            } catch (Exception e) {
                LogManager.logException(getClass(), "Could not update slug for Tenant ID " + tenant.getId(), e);
            }
        } else {
            LogManager.logError(getClass(), "Could not update slug for Tenant ID " + tenant.getId() + ": A Tenant with this slug already exists.");
        }
    }

    /**
     * Changes whether the provided tenant is active or not.
     *
     * @param tenant The tenant to make active/inactive.
     * @param active Whether the tenant should be active.
     */
    private void changeTenantActive(Tenant tenant, boolean active) {
        if (tenant.isActive() == active)
            return;

        tenant.setActive(active);
        try {
            tenant = tenantService.merge(tenant);
            LogManager.logInfo(getClass(), "Set Tenant ID " + tenant.getId() + (active ? " Active." : " Inactive."));
        } catch (Exception e) {
            LogManager.logException(getClass(), "Could not set Tenant ID " + tenant.getId() + (active ? " Active" : " Inactive"), e);
        }
    }

    /**
     * Creates a new tenant using the specified subscription ID and slug.
     *
     * @param subscriptionId The ID of the tenant's subscription (from WooCommerce)
     * @param slug           The slug of the tenant.
     * @return The newly created tenant, unless one already existed with the specified parameters, or the slug was null.
     */
    private Tenant createNewTenant(int subscriptionId, String slug) {
        if (slug == null || slug.isEmpty())
            return null;

        if (tenantService.getTenantBySlug(slug) != null)
            return null;
        if (tenantService.getTenantBySubscriptionId(subscriptionId) != null)
            return null;

        Tenant tenant = new Tenant();
        tenant.setActive(true);
        tenant.setSlug(slug);
        tenant.setSubscriptionId(subscriptionId);

        try {
            tenantService.insert(tenant);
            LogManager.logInfo(getClass(), "Created new Tenant with ID " + tenant.getId() + ", Subscription ID " + tenant.getSubscriptionId() + ", and Slug " + tenant.getSlug());
            return tenant;
        } catch (Exception e) {
            LogManager.logException(getClass(), "Could not create Tenant for Subscription ID " + tenant.getSubscriptionId(), e);
            return null;
        }
    }

    /**
     * Deletes the provided tenant and all of its data.
     *
     * @param tenant The tenant to delete.
     */
    private void deleteTenant(Tenant tenant) {
        try {
            int tenantId = tenant.getId();
            tenantService.delete(tenant.getId());
            LogManager.logInfo(getClass(), "Deleted Tenant with ID " + tenantId + " due to being inactive for 30 days.");
        } catch (Exception e) {
            LogManager.logException(getClass(), "Could not delete Tenant with ID " + tenant.getId(), e);
        }
    }

}
