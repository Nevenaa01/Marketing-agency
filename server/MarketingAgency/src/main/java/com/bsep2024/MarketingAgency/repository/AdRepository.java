package com.bsep2024.MarketingAgency.repository;

import com.bsep2024.MarketingAgency.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    @Query("SELECT a FROM Ad a WHERE a.employeeId =?1")
    Optional<List<Ad>> findAllByEmployeeId(Long employeeId);

    @Query("SELECT a FROM Ad a WHERE a.requestId =?1")
    Optional<Ad> findByRequestId(Long requestId);

    @Query("SELECT a FROM Ad a WHERE a.clientId =?1")
    Optional<List<Ad>> findAllByClientId(Long clientId);

    void deleteByEmployeeId(Long userId);

    void deleteByClientId(Long userId);
}
