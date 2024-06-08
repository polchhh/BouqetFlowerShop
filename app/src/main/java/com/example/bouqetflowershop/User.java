package com.example.bouqetflowershop;

public class User {
    public String id, name, second_name, phone_number, email, imageUri;
    public Boolean is_admin;
    public Integer bonuses;

    public User(){
        this.bonuses = 0;
    }

    public User(String id, String name, String second_name, String phone_number, String email, Boolean is_admin) {
        this.id = id;
        this.name = name;
        this.second_name = second_name;
        this.phone_number = phone_number;
        this.email = email;
        this.is_admin = is_admin;
    }

    public Integer getBonuses() {
        return bonuses;
    }

    public void setBonuses(Integer bonuses) {
        this.bonuses = bonuses;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIs_admin() {
        return is_admin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }
}
