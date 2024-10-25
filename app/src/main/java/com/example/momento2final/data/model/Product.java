package com.example.momento2final.data.model;

public class Product {
    private String id;
    private String Name;
    private float Price;

    public Product() {
    }

    public Product(String name, float price) {
        Name = name;
        Price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }
}
