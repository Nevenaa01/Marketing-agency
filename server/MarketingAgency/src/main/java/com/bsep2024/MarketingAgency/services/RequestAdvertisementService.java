package com.bsep2024.MarketingAgency.services;

import com.bsep2024.MarketingAgency.controllers.RequestAdvertisementController;
import com.bsep2024.MarketingAgency.models.RequestAdvertisement;
import com.bsep2024.MarketingAgency.payload.response.MessageResponse;
import com.bsep2024.MarketingAgency.repository.RequestAdvertisementRepository;
import com.bsep2024.MarketingAgency.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestAdvertisementService {

    private static final Logger logger = LoggerFactory.getLogger(RequestAdvertisementController.class);
    private final RequestAdvertisementRepository requestAdvertisementRepository;

    @Autowired
    public RequestAdvertisementService(RequestAdvertisementRepository requestAdvertisementRepository) {
        this.requestAdvertisementRepository = requestAdvertisementRepository;
    }

    public ResponseEntity<?> createRequestAdvertisement(RequestAdvertisement requestAdvertisement) {
        try {
            RequestAdvertisement ra = new RequestAdvertisement(
                    requestAdvertisement.getId(),
                    requestAdvertisement.getUserId(),
                    requestAdvertisement.getDeadlineDate(),
                    requestAdvertisement.getActiveFrom(),
                    requestAdvertisement.getActiveTo(),
                    requestAdvertisement.getDescription()
            );

            if(ra.Validate()){
                requestAdvertisementRepository.save(ra);
                return ResponseEntity.ok().body(new MessageResponse("Successfully created advertisement request!"));
            }else{
                return ResponseEntity.badRequest().body(new MessageResponse("Badly set dates!"));

            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to create advertisement request. " + e.getMessage()));
        }
    }



    public ResponseEntity<String> deleteRequestAdvertisement(Long id) {
        try {
            Optional<RequestAdvertisement> requestAdvertisementOptional = requestAdvertisementRepository.findById(id);
            if (requestAdvertisementOptional.isPresent()) {
                requestAdvertisementRepository.deleteById(id);
                return ResponseEntity.ok("Object deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Object not found.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error occurred while deleting the object: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    public ResponseEntity<?> getRequestAvertisementByUserId(Long userId) {
        return ResponseEntity.ok(requestAdvertisementRepository.findByUserId(userId));
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(requestAdvertisementRepository.findAll());
    }
}
