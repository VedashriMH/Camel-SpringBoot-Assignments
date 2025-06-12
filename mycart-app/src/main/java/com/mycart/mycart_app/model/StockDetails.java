package com.mycart.mycart_app.model;

public class StockDetails {

    private int availableStock;
    private String unitOfMeasure;

    public StockDetails() {
    }

    public StockDetails(int availableStock, String unitOfMeasure) {
        this.availableStock = availableStock;
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
