package com.bsep2024.MarketingAgency.models;

import java.util.HashSet;
import java.util.Set;

import com.bsep2024.MarketingAgency.utils.AESUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import javax.crypto.SecretKey;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email")
       })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 512)
  private String email;

  @NotBlank
  @Size(max = 256)
  private String emailHash;

  @NotBlank
  @Size(max = 120)
  private String password;

  @NotBlank
  @Size(max = 20)
  private String name;

  @NotBlank
  @Size(max = 20)
  private String surname;

  @NotBlank
  @Size(max = 256)
  private String phoneNumber;

  @NotBlank
  @Size(max = 512)
  private String adress;

  @NotBlank
  @Size(max = 256)
  private String city;

  @NotBlank
  @Size(max = 20)
  private String country;

  private boolean isVerified;

  private EPackage packageType;

  private EUserStatus status;

  private String emailToken;

  private boolean firstTimeLogin;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", 
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  public Set<Permission> getPermissions() {
    Set<Permission> permissions = new HashSet<>();
    for (Role role : roles) {
      permissions.addAll(role.getPermissions());
    }
    return permissions;
  }

  public User() {
  }

  public User(String email, String emailHash, String password, String name, String surname, String phoneNumber, String adress, String city, String country,String token, boolean firstTimeLogin) {
    this.email = email;
    this.emailHash = emailHash;
    this.password = password;
    this.name = name;
    this.surname = surname;
    this.phoneNumber = phoneNumber;
    this.adress = adress;
    this.city = city;
    this.country = country;
    this.emailToken=token;
    this.firstTimeLogin = firstTimeLogin;
  }

  public String getEmailHash() {
    return emailHash;
  }

  public void setEmailHash(String emailHash) {
    this.emailHash = emailHash;
  }

  public boolean isFirstTimeLogin() {
    return firstTimeLogin;
  }

  public void setFirstTimeLogin(boolean firstTimeLogin) {
    this.firstTimeLogin = firstTimeLogin;
  }

  public String getEmailToken() {
    return emailToken;
  }

  public void setEmailToken(String emailToken) {
    this.emailToken = emailToken;
  }

  public EPackage getPackageType() {
    return packageType;
  }

  public void setPackageType(EPackage packageType) {
    this.packageType = packageType;
  }

  public EUserStatus getStatus() {
    return status;
  }

  public void setStatus(EUserStatus status) {
    this.status = status;
  }

  public boolean isVerified() {
    return isVerified;
  }

  public void setVerified(boolean verified) {
    isVerified = verified;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void decryptFields(SecretKey secretKey) throws Exception {
    this.email = AESUtil.decrypt(this.email, secretKey);
    this.phoneNumber = AESUtil.decrypt(this.phoneNumber, secretKey);
    this.adress = AESUtil.decrypt(this.adress, secretKey);
    this.city = AESUtil.decrypt(this.city, secretKey);
  }

  public void encryptFields(SecretKey secretKey) throws Exception{
    this.email = AESUtil.encrypt(this.email, secretKey);
    this.phoneNumber = AESUtil.encrypt(this.phoneNumber, secretKey);
    this.adress = AESUtil.encrypt(this.adress, secretKey);
    this.city = AESUtil.encrypt(this.city, secretKey);
  }
}
