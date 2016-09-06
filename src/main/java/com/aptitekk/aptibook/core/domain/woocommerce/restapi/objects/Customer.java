
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
    "created_at",
    "last_update",
    "email",
    "first_name",
    "last_name",
    "username",
    "role",
    "last_order_id",
    "last_order_date",
    "orders_count",
    "total_spent",
    "avatar_url",
    "billing_address",
    "shipping_address"
})
public class Customer {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("last_update")
    private String lastUpdate;
    @JsonProperty("email")
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("username")
    private String username;
    @JsonProperty("role")
    private String role;
    @JsonProperty("last_order_id")
    private String lastOrderId;
    @JsonProperty("last_order_date")
    private String lastOrderDate;
    @JsonProperty("orders_count")
    private Integer ordersCount;
    @JsonProperty("total_spent")
    private String totalSpent;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("billing_address")
    private BillingAddress billingAddress;
    @JsonProperty("shipping_address")
    private ShippingAddress shippingAddress;
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
     *     The lastUpdate
     */
    @JsonProperty("last_update")
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * 
     * @param lastUpdate
     *     The last_update
     */
    @JsonProperty("last_update")
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * 
     * @return
     *     The email
     */
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The firstName
     */
    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    /**
     * 
     * @param firstName
     *     The first_name
     */
    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 
     * @return
     *     The lastName
     */
    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    /**
     * 
     * @param lastName
     *     The last_name
     */
    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 
     * @return
     *     The username
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     *     The username
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return
     *     The role
     */
    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    /**
     * 
     * @param role
     *     The role
     */
    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 
     * @return
     *     The lastOrderId
     */
    @JsonProperty("last_order_id")
    public String getLastOrderId() {
        return lastOrderId;
    }

    /**
     * 
     * @param lastOrderId
     *     The last_order_id
     */
    @JsonProperty("last_order_id")
    public void setLastOrderId(String lastOrderId) {
        this.lastOrderId = lastOrderId;
    }

    /**
     * 
     * @return
     *     The lastOrderDate
     */
    @JsonProperty("last_order_date")
    public String getLastOrderDate() {
        return lastOrderDate;
    }

    /**
     * 
     * @param lastOrderDate
     *     The last_order_date
     */
    @JsonProperty("last_order_date")
    public void setLastOrderDate(String lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    /**
     * 
     * @return
     *     The ordersCount
     */
    @JsonProperty("orders_count")
    public Integer getOrdersCount() {
        return ordersCount;
    }

    /**
     * 
     * @param ordersCount
     *     The orders_count
     */
    @JsonProperty("orders_count")
    public void setOrdersCount(Integer ordersCount) {
        this.ordersCount = ordersCount;
    }

    /**
     * 
     * @return
     *     The totalSpent
     */
    @JsonProperty("total_spent")
    public String getTotalSpent() {
        return totalSpent;
    }

    /**
     * 
     * @param totalSpent
     *     The total_spent
     */
    @JsonProperty("total_spent")
    public void setTotalSpent(String totalSpent) {
        this.totalSpent = totalSpent;
    }

    /**
     * 
     * @return
     *     The avatarUrl
     */
    @JsonProperty("avatar_url")
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 
     * @param avatarUrl
     *     The avatar_url
     */
    @JsonProperty("avatar_url")
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 
     * @return
     *     The billingAddress
     */
    @JsonProperty("billing_address")
    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    /**
     * 
     * @param billingAddress
     *     The billing_address
     */
    @JsonProperty("billing_address")
    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * 
     * @return
     *     The shippingAddress
     */
    @JsonProperty("shipping_address")
    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    /**
     * 
     * @param shippingAddress
     *     The shipping_address
     */
    @JsonProperty("shipping_address")
    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
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
