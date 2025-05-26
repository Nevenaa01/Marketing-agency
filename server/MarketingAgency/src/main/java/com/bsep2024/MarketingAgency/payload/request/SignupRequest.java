package com.bsep2024.MarketingAgency.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;
 
public class SignupRequest {
    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String surname;

    @NotBlank
    @Size(max = 15)
    private String phoneNumber;

    @NotBlank
    @Size(max = 20)
    private String adress;

    @NotBlank
    @Size(max = 20)
    private String city;

    @NotBlank
    @Size(max = 20)
    private String country;
 
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    private String packageType;
    
    private Set<String> role;
    
    @NotBlank
    @Size(min = 8, max = 40)
    private String password;

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRole() {
      return this.role;
    }
    
    public void setRole(Set<String> role) {
      this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}
