package com.mycart.mycart_app.model;

public class ItemResponse {
    private String _id;
    private String itemName;
    private String categoryName;
    private ItemPrice itemPrice;
    private StockDetails stockDetails;
    private boolean specialProduct;

    // Getters and setters

    public ItemResponse() {
    }

    public ItemResponse(String _id, String itemName, String categoryName, ItemPrice itemPrice, StockDetails stockDetails, boolean specialProduct) {
        this._id = _id;
        this.itemName = itemName;
        this.categoryName = categoryName;
        this.itemPrice = itemPrice;
        this.stockDetails = stockDetails;
        this.specialProduct = specialProduct;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ItemPrice getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(ItemPrice itemPrice) {
        this.itemPrice = itemPrice;
    }

    public StockDetails getStockDetails() {
        return stockDetails;
    }

    public void setStockDetails(StockDetails stockDetails) {
        this.stockDetails = stockDetails;
    }

    public boolean isSpecialProduct() {
        return specialProduct;
    }

    public void setSpecialProduct(boolean specialProduct) {
        this.specialProduct = specialProduct;
    }
}

