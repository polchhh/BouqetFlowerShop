package com.example.bouqetflowershop;

import java.util.ArrayList;

public class OrderData {
    private ArrayList<BouqetCard> bouqets;
    private String selectedDate;
    private String selectedTime;
    private Address address;
    private String id;
    private Double itogCost;

    public String getBouquetNames() {
        StringBuilder bouquetNames = new StringBuilder();
        for (BouqetCard bouquet : bouqets) {
            bouquetNames.append(bouquet.getName()).append(", ");
        }
        if (bouquetNames.length() > 0) {
            bouquetNames.delete(bouquetNames.length() - 2, bouquetNames.length());
        }
        return bouquetNames.toString();
    }

    public ArrayList<BouqetCard> getBouqets() {
        return bouqets;
    }

    public void setBouqets(ArrayList<BouqetCard> bouqets) {
        this.bouqets = bouqets;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getItogCost() {
        return itogCost;
    }

    public void setItogCost(Double itogCost) {
        this.itogCost = itogCost;
    }

    public static class Address {
        private String city;
        private String street;
        private String numberHouse;
        private String coorpuseHouse;
        private String houseApart;
        private String housePod;
        private String houseFloor;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getNumberHouse() {
            return numberHouse;
        }

        public void setNumberHouse(String numberHouse) {
            this.numberHouse = numberHouse;
        }

        public String getCoorpuseHouse() {
            return coorpuseHouse;
        }

        public void setCoorpuseHouse(String coorpuseHouse) {
            this.coorpuseHouse = coorpuseHouse;
        }

        public String getHouseApart() {
            return houseApart;
        }

        public void setHouseApart(String houseApart) {
            this.houseApart = houseApart;
        }

        public String getHousePod() {
            return housePod;
        }

        public void setHousePod(String housePod) {
            this.housePod = housePod;
        }

        public String getHouseFloor() {
            return houseFloor;
        }

        public void setHouseFloor(String houseFloor) {
            this.houseFloor = houseFloor;
        }
    }
}