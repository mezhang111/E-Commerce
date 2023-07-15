package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.annotation.Id;

import java.util.Objects;

// tag::code[]
public class Item {

    private @Id String id;
    private String name;
    private String description;
    private double price;

    private Item() {}

    // public Item(String name, double price) {
    // 	this.name = name;
    // 	this.price = price;
    // }

    public Item(String name, String description, double price) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
    // end::code[]

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return Double.compare(item.price, price) == 0 && Objects.equals(id, item.id) && Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + '}';
    }
}