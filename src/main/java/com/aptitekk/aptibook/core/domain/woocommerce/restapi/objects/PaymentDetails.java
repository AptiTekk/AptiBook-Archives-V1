
/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.woocommerce.restapi.objects;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "method_id",
    "method_title",
    "paid"
})
public class PaymentDetails {

    @JsonProperty("method_id")
    private String methodId;
    @JsonProperty("method_title")
    private String methodTitle;
    @JsonProperty("paid")
    private Boolean paid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The methodId
     */
    @JsonProperty("method_id")
    public String getMethodId() {
        return methodId;
    }

    /**
     * 
     * @param methodId
     *     The method_id
     */
    @JsonProperty("method_id")
    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    /**
     * 
     * @return
     *     The methodTitle
     */
    @JsonProperty("method_title")
    public String getMethodTitle() {
        return methodTitle;
    }

    /**
     * 
     * @param methodTitle
     *     The method_title
     */
    @JsonProperty("method_title")
    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    /**
     * 
     * @return
     *     The paid
     */
    @JsonProperty("paid")
    public Boolean getPaid() {
        return paid;
    }

    /**
     * 
     * @param paid
     *     The paid
     */
    @JsonProperty("paid")
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
