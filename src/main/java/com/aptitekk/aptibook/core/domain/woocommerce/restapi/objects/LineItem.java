
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
    "subtotal",
    "subtotal_tax",
    "total",
    "total_tax",
    "price",
    "quantity",
    "tax_class",
    "name",
    "product_id",
    "sku",
    "meta"
})
public class LineItem {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("subtotal")
    private String subtotal;
    @JsonProperty("subtotal_tax")
    private String subtotalTax;
    @JsonProperty("total")
    private String total;
    @JsonProperty("total_tax")
    private String totalTax;
    @JsonProperty("price")
    private String price;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("tax_class")
    private Object taxClass;
    @JsonProperty("name")
    private String name;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("sku")
    private String sku;
    @JsonProperty("meta")
    private List<Object> meta = new ArrayList<Object>();
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
     *     The subtotalTax
     */
    @JsonProperty("subtotal_tax")
    public String getSubtotalTax() {
        return subtotalTax;
    }

    /**
     * 
     * @param subtotalTax
     *     The subtotal_tax
     */
    @JsonProperty("subtotal_tax")
    public void setSubtotalTax(String subtotalTax) {
        this.subtotalTax = subtotalTax;
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
     *     The price
     */
    @JsonProperty("price")
    public String getPrice() {
        return price;
    }

    /**
     * 
     * @param price
     *     The price
     */
    @JsonProperty("price")
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * 
     * @return
     *     The quantity
     */
    @JsonProperty("quantity")
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 
     * @param quantity
     *     The quantity
     */
    @JsonProperty("quantity")
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 
     * @return
     *     The taxClass
     */
    @JsonProperty("tax_class")
    public Object getTaxClass() {
        return taxClass;
    }

    /**
     * 
     * @param taxClass
     *     The tax_class
     */
    @JsonProperty("tax_class")
    public void setTaxClass(Object taxClass) {
        this.taxClass = taxClass;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The productId
     */
    @JsonProperty("product_id")
    public Integer getProductId() {
        return productId;
    }

    /**
     * 
     * @param productId
     *     The product_id
     */
    @JsonProperty("product_id")
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 
     * @return
     *     The sku
     */
    @JsonProperty("sku")
    public String getSku() {
        return sku;
    }

    /**
     * 
     * @param sku
     *     The sku
     */
    @JsonProperty("sku")
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * 
     * @return
     *     The meta
     */
    @JsonProperty("meta")
    public List<Object> getMeta() {
        return meta;
    }

    /**
     * 
     * @param meta
     *     The meta
     */
    @JsonProperty("meta")
    public void setMeta(List<Object> meta) {
        this.meta = meta;
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
