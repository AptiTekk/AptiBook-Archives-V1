
/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.domain.woocommerce.restapi.objects;

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
    "currency",
    "total",
    "subtotal",
    "total_line_items_quantity",
    "total_tax",
    "total_shipping",
    "cart_tax",
    "shipping_tax",
    "total_discount",
    "shipping_methods",
    "payment_details",
    "billing_address",
    "shipping_address",
    "note",
    "customer_ip",
    "customer_user_agent",
    "customer_id",
    "view_order_url",
    "line_items",
    "shipping_lines",
    "tax_lines",
    "fee_lines",
    "coupon_lines",
    "is_vat_exempt",
    "customer",
    "billing_schedule",
    "parent_order_id"
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
    private String status;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("total")
    private String total;
    @JsonProperty("subtotal")
    private String subtotal;
    @JsonProperty("total_line_items_quantity")
    private Integer totalLineItemsQuantity;
    @JsonProperty("total_tax")
    private String totalTax;
    @JsonProperty("total_shipping")
    private String totalShipping;
    @JsonProperty("cart_tax")
    private String cartTax;
    @JsonProperty("shipping_tax")
    private String shippingTax;
    @JsonProperty("total_discount")
    private String totalDiscount;
    @JsonProperty("shipping_methods")
    private String shippingMethods;
    @JsonProperty("payment_details")
    private PaymentDetails paymentDetails;
    @JsonProperty("billing_address")
    private BillingAddress billingAddress;
    @JsonProperty("shipping_address")
    private ShippingAddress shippingAddress;
    @JsonProperty("note")
    private String note;
    @JsonProperty("customer_ip")
    private String customerIp;
    @JsonProperty("customer_user_agent")
    private String customerUserAgent;
    @JsonProperty("customer_id")
    private Integer customerId;
    @JsonProperty("view_order_url")
    private String viewOrderUrl;
    @JsonProperty("line_items")
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    @JsonProperty("shipping_lines")
    private List<Object> shippingLines = new ArrayList<Object>();
    @JsonProperty("tax_lines")
    private List<TaxLine> taxLines = new ArrayList<TaxLine>();
    @JsonProperty("fee_lines")
    private List<Object> feeLines = new ArrayList<Object>();
    @JsonProperty("coupon_lines")
    private List<Object> couponLines = new ArrayList<Object>();
    @JsonProperty("is_vat_exempt")
    private Boolean isVatExempt;
    @JsonProperty("customer")
    private Customer customer;
    @JsonProperty("billing_schedule")
    private BillingSchedule billingSchedule;
    @JsonProperty("parent_order_id")
    private Integer parentOrderId;
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
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The currency
     */
    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    /**
     * 
     * @param currency
     *     The currency
     */
    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
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
     *     The subtotal
     */
    @JsonProperty("subtotal")
    public String getSubtotal() {
        return subtotal;
    }

    /**
     * 
     * @param subtotal
     *     The subtotal
     */
    @JsonProperty("subtotal")
    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
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
     *     The totalTax
     */
    @JsonProperty("total_tax")
    public String getTotalTax() {
        return totalTax;
    }

    /**
     * 
     * @param totalTax
     *     The total_tax
     */
    @JsonProperty("total_tax")
    public void setTotalTax(String totalTax) {
        this.totalTax = totalTax;
    }

    /**
     * 
     * @return
     *     The totalShipping
     */
    @JsonProperty("total_shipping")
    public String getTotalShipping() {
        return totalShipping;
    }

    /**
     * 
     * @param totalShipping
     *     The total_shipping
     */
    @JsonProperty("total_shipping")
    public void setTotalShipping(String totalShipping) {
        this.totalShipping = totalShipping;
    }

    /**
     * 
     * @return
     *     The cartTax
     */
    @JsonProperty("cart_tax")
    public String getCartTax() {
        return cartTax;
    }

    /**
     * 
     * @param cartTax
     *     The cart_tax
     */
    @JsonProperty("cart_tax")
    public void setCartTax(String cartTax) {
        this.cartTax = cartTax;
    }

    /**
     * 
     * @return
     *     The shippingTax
     */
    @JsonProperty("shipping_tax")
    public String getShippingTax() {
        return shippingTax;
    }

    /**
     * 
     * @param shippingTax
     *     The shipping_tax
     */
    @JsonProperty("shipping_tax")
    public void setShippingTax(String shippingTax) {
        this.shippingTax = shippingTax;
    }

    /**
     * 
     * @return
     *     The totalDiscount
     */
    @JsonProperty("total_discount")
    public String getTotalDiscount() {
        return totalDiscount;
    }

    /**
     * 
     * @param totalDiscount
     *     The total_discount
     */
    @JsonProperty("total_discount")
    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    /**
     * 
     * @return
     *     The shippingMethods
     */
    @JsonProperty("shipping_methods")
    public String getShippingMethods() {
        return shippingMethods;
    }

    /**
     * 
     * @param shippingMethods
     *     The shipping_methods
     */
    @JsonProperty("shipping_methods")
    public void setShippingMethods(String shippingMethods) {
        this.shippingMethods = shippingMethods;
    }

    /**
     * 
     * @return
     *     The paymentDetails
     */
    @JsonProperty("payment_details")
    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    /**
     * 
     * @param paymentDetails
     *     The payment_details
     */
    @JsonProperty("payment_details")
    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
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
     *     The customerIp
     */
    @JsonProperty("customer_ip")
    public String getCustomerIp() {
        return customerIp;
    }

    /**
     * 
     * @param customerIp
     *     The customer_ip
     */
    @JsonProperty("customer_ip")
    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp;
    }

    /**
     * 
     * @return
     *     The customerUserAgent
     */
    @JsonProperty("customer_user_agent")
    public String getCustomerUserAgent() {
        return customerUserAgent;
    }

    /**
     * 
     * @param customerUserAgent
     *     The customer_user_agent
     */
    @JsonProperty("customer_user_agent")
    public void setCustomerUserAgent(String customerUserAgent) {
        this.customerUserAgent = customerUserAgent;
    }

    /**
     * 
     * @return
     *     The customerId
     */
    @JsonProperty("customer_id")
    public Integer getCustomerId() {
        return customerId;
    }

    /**
     * 
     * @param customerId
     *     The customer_id
     */
    @JsonProperty("customer_id")
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    /**
     * 
     * @return
     *     The viewOrderUrl
     */
    @JsonProperty("view_order_url")
    public String getViewOrderUrl() {
        return viewOrderUrl;
    }

    /**
     * 
     * @param viewOrderUrl
     *     The view_order_url
     */
    @JsonProperty("view_order_url")
    public void setViewOrderUrl(String viewOrderUrl) {
        this.viewOrderUrl = viewOrderUrl;
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
     * 
     * @return
     *     The shippingLines
     */
    @JsonProperty("shipping_lines")
    public List<Object> getShippingLines() {
        return shippingLines;
    }

    /**
     * 
     * @param shippingLines
     *     The shipping_lines
     */
    @JsonProperty("shipping_lines")
    public void setShippingLines(List<Object> shippingLines) {
        this.shippingLines = shippingLines;
    }

    /**
     * 
     * @return
     *     The taxLines
     */
    @JsonProperty("tax_lines")
    public List<TaxLine> getTaxLines() {
        return taxLines;
    }

    /**
     * 
     * @param taxLines
     *     The tax_lines
     */
    @JsonProperty("tax_lines")
    public void setTaxLines(List<TaxLine> taxLines) {
        this.taxLines = taxLines;
    }

    /**
     * 
     * @return
     *     The feeLines
     */
    @JsonProperty("fee_lines")
    public List<Object> getFeeLines() {
        return feeLines;
    }

    /**
     * 
     * @param feeLines
     *     The fee_lines
     */
    @JsonProperty("fee_lines")
    public void setFeeLines(List<Object> feeLines) {
        this.feeLines = feeLines;
    }

    /**
     * 
     * @return
     *     The couponLines
     */
    @JsonProperty("coupon_lines")
    public List<Object> getCouponLines() {
        return couponLines;
    }

    /**
     * 
     * @param couponLines
     *     The coupon_lines
     */
    @JsonProperty("coupon_lines")
    public void setCouponLines(List<Object> couponLines) {
        this.couponLines = couponLines;
    }

    /**
     * 
     * @return
     *     The isVatExempt
     */
    @JsonProperty("is_vat_exempt")
    public Boolean getIsVatExempt() {
        return isVatExempt;
    }

    /**
     * 
     * @param isVatExempt
     *     The is_vat_exempt
     */
    @JsonProperty("is_vat_exempt")
    public void setIsVatExempt(Boolean isVatExempt) {
        this.isVatExempt = isVatExempt;
    }

    /**
     * 
     * @return
     *     The customer
     */
    @JsonProperty("customer")
    public Customer getCustomer() {
        return customer;
    }

    /**
     * 
     * @param customer
     *     The customer
     */
    @JsonProperty("customer")
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * 
     * @return
     *     The billingSchedule
     */
    @JsonProperty("billing_schedule")
    public BillingSchedule getBillingSchedule() {
        return billingSchedule;
    }

    /**
     * 
     * @param billingSchedule
     *     The billing_schedule
     */
    @JsonProperty("billing_schedule")
    public void setBillingSchedule(BillingSchedule billingSchedule) {
        this.billingSchedule = billingSchedule;
    }

    /**
     * 
     * @return
     *     The parentOrderId
     */
    @JsonProperty("parent_order_id")
    public Integer getParentOrderId() {
        return parentOrderId;
    }

    /**
     * 
     * @param parentOrderId
     *     The parent_order_id
     */
    @JsonProperty("parent_order_id")
    public void setParentOrderId(Integer parentOrderId) {
        this.parentOrderId = parentOrderId;
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
