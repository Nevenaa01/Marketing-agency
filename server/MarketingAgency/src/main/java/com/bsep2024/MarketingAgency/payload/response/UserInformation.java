package com.bsep2024.MarketingAgency.payload.response;


import com.bsep2024.MarketingAgency.models.EPackage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserInformation {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String surname;
    @NotBlank
    private String adress;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @NotBlank
    private String phoneNumber;
    private EPackage packageType;

    public UserInformation(){}

    public UserInformation(Long id, String name, String email, String surname, String adress, String city, String country, String phoneNumber, EPackage packageType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.surname = surname;
        this.adress = adress;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.packageType = packageType;
    }

    public EPackage getPackageType() {
        return packageType;
    }

    public void setPackageType(EPackage packageType) {
        this.packageType = packageType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    }
