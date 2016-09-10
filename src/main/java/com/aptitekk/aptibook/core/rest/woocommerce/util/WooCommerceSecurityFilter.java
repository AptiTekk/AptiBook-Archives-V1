/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.rest.woocommerce.util;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

public class WooCommerceSecurityFilter implements ClientRequestFilter {

    private static final String CONSUMER_KEY_PARAM = "consumer_key";
    private static final String CONSUMER_SECRET_PARAM = "consumer_secret";

    private String consumerKey;
    private String consumerSecret;

    public WooCommerceSecurityFilter(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public void filter(ClientRequestContext requestContext) throws IOException {
        if (consumerKey == null || consumerSecret == null)
            return;

        requestContext.setUri(
                UriBuilder.fromUri(
                        requestContext.getUri())
                        .queryParam(CONSUMER_KEY_PARAM, consumerKey)
                        .queryParam(CONSUMER_SECRET_PARAM, consumerSecret)
                        .build());
    }
}
