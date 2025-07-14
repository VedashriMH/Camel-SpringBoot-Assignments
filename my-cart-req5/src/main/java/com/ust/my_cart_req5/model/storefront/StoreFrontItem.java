package com.ust.my_cart_req5.model.storefront;


public class StoreFrontItem {
    private String id;
    private String itemName;
    private String categoryName;
    private Price itemPrice;
    private Stock stockDetails;
    private boolean specialProduct;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Price getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Price itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Stock getStockDetails() {
        return stockDetails;
    }

    public void setStockDetails(Stock stockDetails) {
        this.stockDetails = stockDetails;
    }

    public boolean isSpecialProduct() {
        return specialProduct;
    }

    public void setSpecialProduct(boolean specialProduct) {
        this.specialProduct = specialProduct;
    }
}
