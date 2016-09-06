/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.woocommerce.restapi;


import com.aptitekk.aptibook.core.domain.woocommerce.restapi.objects.Subscription;
import com.aptitekk.aptibook.core.domain.woocommerce.restapi.objects.Subscriptions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/subscriptions")
public interface SubscriptionService {

    @GET
    @Path("/count")
    int count() throws WebApplicationException;

    @GET
    @Path("/statuses")
    Map<String, String> getStatuses() throws WebApplicationException;

    @GET
    Subscriptions getAll() throws WebApplicationException;

    @GET
    @Path("/{id}")
    Subscription get(@PathParam("id") int id) throws WebApplicationException;


}
