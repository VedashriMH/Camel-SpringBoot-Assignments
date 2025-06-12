package com.mycart.mycart_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
    private String _id;
    private String itemName;
    private String categoryId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String  lastUpdateDate;
    private ItemPrice itemPrice;
    private StockDetails stockDetails;
    private boolean specialProduct;
    private List<Review> review;

    public Item() {
    }

    public Item(String _id, String itemName, String categoryId, String lastUpdateDate, ItemPrice itemPrice, StockDetails stockDetails,boolean specialProduct, List<Review> review) {
        this._id = _id;
        this.itemName = itemName;
        this.categoryId = categoryId;
        this.lastUpdateDate = lastUpdateDate;
        this.itemPrice = itemPrice;
        this.specialProduct = specialProduct;
        this.stockDetails = stockDetails;
        this.review = review;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String  lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public ItemPrice getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(ItemPrice itemPrice) {
        this.itemPrice = itemPrice;
    }

    public boolean isSpecialProduct() {
        return specialProduct;
    }

    public void setSpecialProduct(boolean specialProduct) {
        this.specialProduct = specialProduct;
    }

    public StockDetails getStockDetails() {
        return stockDetails;
    }

    public void setStockDetails(StockDetails stockDetails) {
        this.stockDetails = stockDetails;
    }

    public List<Review> getReview() {
        return review;
    }

    public void setReview(List<Review> review) {
        this.review = review;
    }

}

