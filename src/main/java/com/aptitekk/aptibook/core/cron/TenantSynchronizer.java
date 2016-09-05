/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.cron;

import com.aptitekk.aptibook.core.domain.services.TenantService;
import com.aptitekk.aptibook.core.tenant.TenantManagementService;
import com.aptitekk.aptibook.core.util.LogManager;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
public class TenantSynchronizer {

    private final static String SUBSCRIPTIONS_INFO_URL = "http://aptitekk.com/wc-api/v3/subscriptions";
    private final static String WOOCOMMERCE_CK = System.getenv("WOOCOMMERCE_CK");
    private final static String WOOCOMMERCE_CS = System.getenv("WOOCOMMERCE_CS");

    @Inject
    private TenantService tenantService;

    @Inject
    private TenantManagementService tenantManagementService;

    @Schedule(second = "*/10", minute = "*", hour = "*")
    private void synchronizeTenants() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(SUBSCRIPTIONS_INFO_URL);
        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).get();
        LogManager.logInfo("Response: " + response.getStatus());
        client.close();
    }

}
