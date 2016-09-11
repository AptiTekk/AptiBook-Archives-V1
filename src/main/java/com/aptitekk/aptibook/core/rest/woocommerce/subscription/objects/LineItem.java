
/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.aptitekk.aptibook.core.rest.woocommerce.subscription.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LineItem {

    @SerializedName("id")
    private Integer id;
    @SerializedName("subtotal")
    private String subtotal;
    @SerializedName("subtotal_tax")
    private String subtotalTax;
    @SerializedName("total")
    private String total;
    @SerializedName("total_tax")
    private String totalTax;
    @SerializedName("price")
    private String price;
    @SerializedName("quantity")
    private Integer quantity;
    @SerializedName("tax_class")
    private Object taxClass;
    @SerializedName("name")
    private String name;
    @SerializedName("product_id")
    private Integer productId;
    @SerializedName("sku")
    private String sku;
    @SerializedName("meta")
    private List<MetaItem> meta = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getSubtotalTax() {
        return subtotalTax;
    }

    public void setSubtotalTax(String subtotalTax) {
        this.subtotalTax = subtotalTax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(String totalTax) {
        this.totalTax = totalTax;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Object getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(Object taxClass) {
        this.taxClass = taxClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<MetaItem> getMeta() {
        return meta;
    }

    public void setMeta(List<MetaItem> meta) {
        this.meta = meta;
    }

}
