package com.ust.my_cart_req5.model.trend;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Category {

    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlElement(name = "categoryName")
    private CategoryName categoryName;

    @XmlElement(name = "items")
    private List<TrendItem> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(CategoryName categoryName) {
        this.categoryName = categoryName;
    }

    public List<TrendItem> getItems() {
        return items;
    }

    public void setItems(List<TrendItem> items) {
        this.items = items;
    }
}