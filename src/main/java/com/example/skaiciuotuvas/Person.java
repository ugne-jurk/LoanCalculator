package com.example.skaiciuotuvas;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Person {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleDoubleProperty sum = new SimpleDoubleProperty();

    public Person(int id, String name, double sum) {
        this.id.set(id);
        this.name.set(name);
        this.sum.set(sum);
    }

    public Person(SimpleIntegerProperty id, SimpleStringProperty name, SimpleDoubleProperty sum) {
        this.id = id;
        this.name = name;
        this.sum = sum;
    }

    public static ObservableList<Person> getSampleData() {
        ObservableList<Person> data = FXCollections.observableArrayList();
        data.add(new Person(1, "John", 100.5));
        data.add(new Person(2, "Jane", 2.5));
        data.add(new Person(3, "Jacob", 12.5));
        return data;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getSum() {
        return sum.get();
    }

    public SimpleDoubleProperty sumProperty() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum.set(sum);
    }
}