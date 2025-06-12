package com.ust.my_cart_req3.model.trend;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    // Example methods — replace with actual model classes
    public Category createCategory() {
        return new Category();
    }

    public CategoryName createCategoryName() {
        return new CategoryName();
    }

    public TrendInventory createTrendInventory() {
        return new TrendInventory();
    }

    public TrendItem createTrendItem() {
        return new TrendItem();
    }

    // Add factory methods for each JAXB-annotated class in this package
}

