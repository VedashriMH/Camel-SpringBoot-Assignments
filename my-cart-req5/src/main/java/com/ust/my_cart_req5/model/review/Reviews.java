package com.ust.my_cart_req5.model.review;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reviews")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reviews {
    @XmlElement(name = "items")
    private List<ReviewItem> items = new ArrayList<>();

    public List<ReviewItem> getItems() {
        return items;
    }

    public void setItems(List<ReviewItem> items) {
        this.items = items;
    }
}
