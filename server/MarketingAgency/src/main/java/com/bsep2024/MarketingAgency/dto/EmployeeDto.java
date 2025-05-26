package com.bsep2024.MarketingAgency.dto;

import com.bsep2024.MarketingAgency.models.EPackage;
import com.bsep2024.MarketingAgency.models.EUserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeeDto {
    private Long id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String adress;
    private String city;
    private String country;

    public EmployeeDto(Long id, String name, String surname, String phoneNumber, String adress, String city, String country) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.adress = adress;
        this.city = city;
        this.country = country;
    }
}
