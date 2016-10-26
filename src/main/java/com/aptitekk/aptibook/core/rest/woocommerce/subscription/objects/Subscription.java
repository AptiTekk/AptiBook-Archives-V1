
/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.rest.woocommerce.subscription.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "order_number",
    "order_key",
    "created_at",
    "updated_at",
    "status",
    "total_line_items_quantity",
    "note",
    "line_items",
})
public class Subscription {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("order_number")
    private Integer orderNumber;
    @JsonProperty("order_key")
    private String orderKey;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("total_line_items_quantity")
    private Integer totalLineItemsQuantity;
    @JsonProperty("note")
    private String note;
    @JsonProperty("line_items")
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    @JsonProperty("billing_address")
    private BillingAddress billingAddress;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

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
     *     The orderNumber
     */
    @JsonProperty("order_number")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    /**
     * 
     * @param orderNumber
     *     The order_number
     */
    @JsonProperty("order_number")
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 
     * @return
     *     The orderKey
     */
    @JsonProperty("order_key")
    public String getOrderKey() {
        return orderKey;
    }

    /**
     * 
     * @param orderKey
     *     The order_key
     */
    @JsonProperty("order_key")
    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updated_at
     */
    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The totalLineItemsQuantity
     */
    @JsonProperty("total_line_items_quantity")
    public Integer getTotalLineItemsQuantity() {
        return totalLineItemsQuantity;
    }

    /**
     * 
     * @param totalLineItemsQuantity
     *     The total_line_items_quantity
     */
    @JsonProperty("total_line_items_quantity")
    public void setTotalLineItemsQuantity(Integer totalLineItemsQuantity) {
        this.totalLineItemsQuantity = totalLineItemsQuantity;
    }

    /**
     * 
     * @return
     *     The note
     */
    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    /**
     * 
     * @param note
     *     The note
     */
    @JsonProperty("note")
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 
     * @return
     *     The lineItems
     */
    @JsonProperty("line_items")
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    /**
     * 
     * @param lineItems
     *     The line_items
     */
    @JsonProperty("line_items")
    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }


    /**
     * @return
     * BillingAddress object
     */
    @JsonProperty
    public BillingAddress getBillingAddress() {
        return billingAddress;
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
