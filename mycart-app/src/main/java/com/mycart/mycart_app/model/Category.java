package com.mycart.mycart_app.model;

public class Category {
    private String _id;
    private String categoryName;
    private String categoryDep;
    private String categoryTax;

    public Category() {
    }

    public Category(String _id, String categoryName, String categoryDep, String categoryTax) {
        this._id = _id;
        this.categoryName = categoryName;
        this.categoryDep = categoryDep;
        this.categoryTax = categoryTax;
    }

    // Getters and setters

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDep() {
        return categoryDep;
    }

    public void setCategoryDep(String categoryDep) {
        this.categoryDep = categoryDep;
    }

    public String getCategoryTax() {
        return categoryTax;
    }

    public void setCategoryTax(String categoryTax) {
        this.categoryTax = categoryTax;
    }
}

