package com.oceanbreezeresorts.hotel.model;

public class Customer {
    private String first_name;
    private String last_name;
    private String mobile;
    private String password;
    private String email;
    private String verification_code;

    public Customer() {

    }


    public Customer(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Customer(String first_name, String last_name, String mobile, String password, String email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.mobile = mobile;
        this.password = password;
        this.email = email;
    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }
}
