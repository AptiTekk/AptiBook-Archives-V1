/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.cron;

import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.domain.woocommerce.restapi.SubscriptionService;
import com.aptitekk.aptibook.core.domain.woocommerce.restapi.util.WooCommerceSecurityFilter;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.util.LogManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class TenantSynchronizer {

    private final static String SUBSCRIPTIONS_INFO_URL = "https://aptitekk.com/wc-api/v3/";
    private final static String WOOCOMMERCE_CK = System.getenv("WOOCOMMERCE_CK");
    private final static String WOOCOMMERCE_CS = System.getenv("WOOCOMMERCE_CS");

    @Inject
    private TenantService tenantService;

    @Inject
    private TenantManagementService tenantManagementService;

    @Schedule(second = "*/10", minute = "*", hour = "*")
    private void synchronizeTenants() {
        URLConnectionEngine urlConnectionEngine = new URLConnectionEngine();

        ResteasyClientBuilder builder = new ResteasyClientBuilder();
        builder.register(new WooCommerceSecurityFilter(
                WOOCOMMERCE_CK,
                WOOCOMMERCE_CS));
        builder.httpEngine(urlConnectionEngine);
        ResteasyClient webClient = builder.build();
        ResteasyWebTarget webTarget = webClient.target(SUBSCRIPTIONS_INFO_URL);
        SubscriptionService service = webTarget.proxy(SubscriptionService.class);

        LogManager.logInfo("Result: "+service.getAll().getSubscriptions().get(0).getLineItems().get(0).getMeta().get(0));
    }

}
