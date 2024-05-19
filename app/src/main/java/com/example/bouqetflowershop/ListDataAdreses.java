package com.example.bouqetflowershop;

public class ListDataAdreses {
    String city, street;
    String houseNumber;
    String houseCourpose;
    String housePod;
    String houseFloor;
    String houseApart;
    String id;

    public ListDataAdreses(String city, String street, String houseNumber, String houseCourpose, String housePod, String houseFloor, String houseApart, String id) {
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.houseCourpose = houseCourpose;
        this.housePod = housePod;
        this.houseFloor = houseFloor;
        this.houseApart = houseApart;
        this.id = id;
    }

    public ListDataAdreses() {}

    public ListDataAdreses(String city, String street, String houseNumber, String houseCourpose, String housePod, String houseFloor, String houseApart) {
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.houseCourpose = houseCourpose;
        this.housePod = housePod;
        this.houseFloor = houseFloor;
        this.houseApart = houseApart;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getHouseCourpose() {
        return houseCourpose;
    }

    public String getHousePod() {
        return housePod;
    }

    public String getHouseFloor() {
        return houseFloor;
    }

    public String getHouseApart() {
        return houseApart;
    }

    public String getId() {
        return id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setHouseCourpose(String houseCourpose) {
        this.houseCourpose = houseCourpose;
    }

    public void setHousePod(String housePod) {
        this.housePod = housePod;
    }

    public void setHouseFloor(String houseFloor) {
        this.houseFloor = houseFloor;
    }

    public void setHouseApart(String houseApart) {
        this.houseApart = houseApart;
    }

    public void setId(String id) {
        this.id = id;
    }
}
