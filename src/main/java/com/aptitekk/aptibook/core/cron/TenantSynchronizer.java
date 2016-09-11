/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.cron;

import com.aptitekk.aptibook.core.domain.entities.Tenant;
import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.rest.woocommerce.subscription.objects.*;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.util.LogManager;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Singleton
public class TenantSynchronizer {

    private static final String SUBSCRIPTIONS_INFO_URL = "https://aptitekk.com/wc-api/v3/subscriptions/";
    private static final String WOOCOMMERCE_CK = System.getenv("WOOCOMMERCE_CK");
    private static final String WOOCOMMERCE_CS = System.getenv("WOOCOMMERCE_CS");

    private static final int APTIBOOK_PRODUCT_ID = 132;
    private static final String URL_SLUG_META_KEY = "URL Slug";

    @Inject
    private TenantService tenantService;

    @Inject
    private TenantManagementService tenantManagementService;

    @Schedule(minute = "*", hour = "*", persistent = false)
    private void synchronizeTenants() {
        LogManager.logDebug("[TenantSynchronizer] Synchronizing Tenants...");

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
                        if (lineItem.getProductId().equals(APTIBOOK_PRODUCT_ID)) {
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
                                Calendar timeSetInactive = currentTenant.getTimeSetInactive();
                                if (timeSetInactive != null) {
                                    if (new Interval(new DateTime(timeSetInactive.getTime()), new DateTime(DateTimeZone.UTC)).toDuration().getStandardDays() >= 30)
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

        tenantManagementService.refreshTenants();
        LogManager.logDebug("[TenantSynchronizer] Synchronization Complete.");
    }

    /**
     * Contacts WooCommerce and retrieves a list of Subscriptions.
     *
     * @return The list of Subscriptions.
     */
    private List<Subscription> getSubscriptions() {
        if (WOOCOMMERCE_CK == null || WOOCOMMERCE_CS == null || WOOCOMMERCE_CK.isEmpty() || WOOCOMMERCE_CS.isEmpty()) {
            LogManager.logError("[TenantSynchronizer] Could not Synchronize Tenants due to missing WooCommerce Key and/or Secret");
            return null;
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(URI.create(SUBSCRIPTIONS_INFO_URL + "?consumer_key=" + WOOCOMMERCE_CK + "&consumer_secret=" + WOOCOMMERCE_CS));
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                LogManager.logError("[TenantSynchronizer] Could not Synchronize Tenants due to non-okay status: " + response.getStatusLine().getStatusCode());
                return null;
            }

            StringBuilder jsonBuilder = new StringBuilder();
            InputStream inputStream = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            inputStream.close();

            Gson gson = new Gson();
            Subscriptions subscriptions = gson.fromJson(jsonBuilder.toString(), Subscriptions.class);
            try {
                return subscriptions.getSubscriptions();
            } catch (ClientErrorException e) {
                LogManager.logError("[TenantSynchronizer] Could not Synchronize Tenants due to Client Error: " + e.getMessage());
                return null;
            } catch (Exception e) {
                LogManager.logError("[TenantSynchronizer] Could not Synchronize Tenants due to Unknown Error: " + e.getClass().getName() + " - " + e.getMessage());
                return null;
            }
        } catch (IOException e) {
            LogManager.logError("[TenantSynchronizer] Could not Synchronize Tenants due to Unknown Error: " + e.getClass().getName() + " - " + e.getMessage());
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
                LogManager.logDebug("[TenantSynchronizer] Updated Slug For Tenant ID " + tenant.getId() + ". Previously: " + previousSlug + "; Now: " + newSlug);
            } catch (Exception e) {
                LogManager.logError("[TenantSynchronizer] Could not update slug for Tenant ID " + tenant.getId() + ": " + e.getMessage());
            }
        } else {
            LogManager.logError("[TenantSynchronizer] Could not update slug for Tenant ID " + tenant.getId() + ": A Tenant with this slug already exists.");
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
            LogManager.logDebug("[TenantSynchronizer] Set Tenant ID " + tenant.getId() + (active ? " Active." : " Inactive."));
        } catch (Exception e) {
            LogManager.logError("[TenantSynchronizer] Could not set Tenant ID " + tenant.getId() + (active ? " Active" : " Inactive") + ": " + e.getMessage());
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
            LogManager.logDebug("[TenantSynchronizer] Created new Tenant with ID " + tenant.getId() + ", Subscription ID " + tenant.getSubscriptionId() + ", and Slug " + tenant.getSlug());
            return tenant;
        } catch (Exception e) {
            LogManager.logError("[TenantSynchronizer] Could not create Tenant for Subscription ID " + tenant.getSubscriptionId() + ": " + e.getMessage());
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
            LogManager.logDebug("[TenantSynchronizer] Deleted Tenant with ID " + tenantId + " due to being inactive for 30 days.");
        } catch (Exception e) {
            LogManager.logError("[TenantSynchronizer] Could not delete Tenant with ID " + tenant.getId() + ": " + e.getMessage());
        }
    }

}
