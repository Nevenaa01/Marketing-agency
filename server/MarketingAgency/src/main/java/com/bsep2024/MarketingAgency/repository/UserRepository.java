package com.bsep2024.MarketingAgency.repository;

import java.util.List;
import java.util.Optional;

import com.bsep2024.MarketingAgency.payload.response.UserInformation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bsep2024.MarketingAgency.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.name = :name AND u.surname = :surname")
  Optional<User> findByFullName(@Param("name") String name, @Param("surname") String surname);
  Optional<List<User>> findByIsVerified(boolean isVerified);



  Boolean existsByEmail(String email);

  @Query("SELECT new com.bsep2024.MarketingAgency.payload.response.UserInformation(u.id, u.name, u.email, u.surname, u.adress, u.city, u.country, u.phoneNumber, u.packageType) " +
          "FROM User u WHERE u.id = :id")
  Optional<UserInformation> findUserInformationById(@Param("id") Long id);

  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.name =:name, u.surname =:surname, u.phoneNumber =:phoneNumber, u.email =:email, u.adress =:adress, u.city =:city, u.country =:country WHERE u.id =:id")
  int updateUserInformation(@Param("id") Long id, @Param("name") String name, @Param("surname") String surname, @Param("phoneNumber") String phoneNumber, @Param("email") String email, @Param("adress") String adress, @Param("city") String city, @Param("country") String country);


  boolean existsByEmailHash(String emailHash);

  Optional<User> findByEmailHash(String encode);
}
