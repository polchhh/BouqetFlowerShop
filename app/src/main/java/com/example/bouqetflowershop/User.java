package com.example.bouqetflowershop;

public class User {
    public String id, name, second_name, phone_number, email;
    public Boolean is_admin;

    public User(){}

    public User(String id, String name, String second_name, String phone_number, String email, Boolean is_admin) {
        this.id = id;
        this.name = name;
        this.second_name = second_name;
        this.phone_number = phone_number;
        this.email = email;
        this.is_admin = is_admin;
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
}
