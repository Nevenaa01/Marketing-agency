package com.bsep2024.MarketingAgency.repository;

import com.bsep2024.MarketingAgency.models.RequestAdvertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestAdvertisementRepository extends JpaRepository<RequestAdvertisement, Long> {
    List<RequestAdvertisement> findByUserId(Long userId);


    void deleteByUserId(Long userId);
}
