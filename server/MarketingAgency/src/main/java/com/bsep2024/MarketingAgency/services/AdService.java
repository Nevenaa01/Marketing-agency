package com.bsep2024.MarketingAgency.services;

import com.bsep2024.MarketingAgency.models.Ad;
import com.bsep2024.MarketingAgency.repository.AdRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdService {
    @Autowired
    private final AdRepository _adRepository;

    public AdService(AdRepository _adRepository) {
        this._adRepository = _adRepository;
    }
    public List<Ad> findAllByEmployeeId(Long employeeId){
        Optional<List<Ad>> adsOptional = _adRepository.findAllByEmployeeId(employeeId);

        if(adsOptional.isPresent()){
            return adsOptional.get();
        }
        else{
            return null;
        }
    }
    public Ad findByRequestId(Long id){
        Optional<Ad> adOptional = _adRepository.findByRequestId(id);
        if (adOptional.isPresent()) {
            return adOptional.get();
        } else {
            return null;
        }
    }
    public Ad create(Ad ad){
        return _adRepository.save(ad);
    }

    public List<Ad> getAll() { return _adRepository.findAll(); }

    public List<Ad> findAllByClientId(Long clientId){return _adRepository.findAllByClientId(clientId).get();}
}
