package com.mycart.mycart_app.model;

public class ItemPrice {

    private double basePrice;
    private double sellingPrice;

    public ItemPrice() {
    }

    public ItemPrice(double basePrice, double sellingPrice) {
        this.basePrice = basePrice;
        this.sellingPrice = sellingPrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
