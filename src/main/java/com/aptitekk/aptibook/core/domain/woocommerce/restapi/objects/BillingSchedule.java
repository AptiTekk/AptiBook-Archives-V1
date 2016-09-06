
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
    "period",
    "interval",
    "start_at",
    "trial_end_at",
    "next_payment_at",
    "end_at"
})
public class BillingSchedule {

    @JsonProperty("period")
    private String period;
    @JsonProperty("interval")
    private String interval;
    @JsonProperty("start_at")
    private String startAt;
    @JsonProperty("trial_end_at")
    private String trialEndAt;
    @JsonProperty("next_payment_at")
    private String nextPaymentAt;
    @JsonProperty("end_at")
    private String endAt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The period
     */
    @JsonProperty("period")
    public String getPeriod() {
        return period;
    }

    /**
     * 
     * @param period
     *     The period
     */
    @JsonProperty("period")
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * 
     * @return
     *     The interval
     */
    @JsonProperty("interval")
    public String getInterval() {
        return interval;
    }

    /**
     * 
     * @param interval
     *     The interval
     */
    @JsonProperty("interval")
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * 
     * @return
     *     The startAt
     */
    @JsonProperty("start_at")
    public String getStartAt() {
        return startAt;
    }

    /**
     * 
     * @param startAt
     *     The start_at
     */
    @JsonProperty("start_at")
    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    /**
     * 
     * @return
     *     The trialEndAt
     */
    @JsonProperty("trial_end_at")
    public String getTrialEndAt() {
        return trialEndAt;
    }

    /**
     * 
     * @param trialEndAt
     *     The trial_end_at
     */
    @JsonProperty("trial_end_at")
    public void setTrialEndAt(String trialEndAt) {
        this.trialEndAt = trialEndAt;
    }

    /**
     * 
     * @return
     *     The nextPaymentAt
     */
    @JsonProperty("next_payment_at")
    public String getNextPaymentAt() {
        return nextPaymentAt;
    }

    /**
     * 
     * @param nextPaymentAt
     *     The next_payment_at
     */
    @JsonProperty("next_payment_at")
    public void setNextPaymentAt(String nextPaymentAt) {
        this.nextPaymentAt = nextPaymentAt;
    }

    /**
     * 
     * @return
     *     The endAt
     */
    @JsonProperty("end_at")
    public String getEndAt() {
        return endAt;
    }

    /**
     * 
     * @param endAt
     *     The end_at
     */
    @JsonProperty("end_at")
    public void setEndAt(String endAt) {
        this.endAt = endAt;
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
