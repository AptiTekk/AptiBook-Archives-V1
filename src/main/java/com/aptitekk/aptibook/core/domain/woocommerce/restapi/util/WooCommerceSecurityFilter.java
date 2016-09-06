/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.woocommerce.restapi.util;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

/**
 * Created by kevint on 9/5/2016.
 */
public class WooCommerceSecurityFilter implements ClientRequestFilter {

    public static final String CONSUMER_KEY_PARAM = "consumer_key";
    public static final String CONSUMER_SECRET_PARAM = "consumer_secret";

    private String consumerKey;
    private String consumerSecret;

    public WooCommerceSecurityFilter(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.setUri(
                UriBuilder.fromUri(
                        requestContext.getUri())
                        .queryParam(CONSUMER_KEY_PARAM, consumerKey)
                        .queryParam(CONSUMER_SECRET_PARAM, consumerSecret)
                        .build());
    }
}
