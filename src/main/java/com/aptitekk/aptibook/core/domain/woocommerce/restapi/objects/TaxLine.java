
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
    "id",
    "rate_id",
    "code",
    "title",
    "total",
    "compound"
})
public class TaxLine {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("rate_id")
    private String rateId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("title")
    private String title;
    @JsonProperty("total")
    private String total;
    @JsonProperty("compound")
    private Boolean compound;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The rateId
     */
    @JsonProperty("rate_id")
    public String getRateId() {
        return rateId;
    }

    /**
     * 
     * @param rateId
     *     The rate_id
     */
    @JsonProperty("rate_id")
    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    /**
     * 
     * @return
     *     The code
     */
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    /**
     * 
     * @param code
     *     The code
     */
    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The total
     */
    @JsonProperty("total")
    public String getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    @JsonProperty("total")
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * 
     * @return
     *     The compound
     */
    @JsonProperty("compound")
    public Boolean getCompound() {
        return compound;
    }

    /**
     * 
     * @param compound
     *     The compound
     */
    @JsonProperty("compound")
    public void setCompound(Boolean compound) {
        this.compound = compound;
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
