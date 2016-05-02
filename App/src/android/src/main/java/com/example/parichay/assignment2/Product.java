package com.example.parichay.assignment2;

/**
 * Created by Parichay on 2/21/2016.
 */

//defines the object of the customised list used by customised product adapter
public class Product {
    private int id;
    private String code;
    private String name;
    private String ltp;
    private String credits;
    public Product(int id, String code, String name, String ltp, String credits) {
        this.id = id;       //first argument
        this.code = code;   //second argument that is displayed in first line
        this.name = name;   //third argument that is displayed in second line
        this.ltp = ltp;     //fourth argument that is displayed in third line
        this.credits=credits;  //last argument parsed
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLtp() {
        return ltp;
    }

    public void setLtp(String ltp) {
        this.ltp = ltp;
    }
}
