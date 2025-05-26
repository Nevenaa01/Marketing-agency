package com.bsep2024.MarketingAgency.controllers;

import com.bsep2024.MarketingAgency.models.RequestAdvertisement;
import com.bsep2024.MarketingAgency.models.User;
import com.bsep2024.MarketingAgency.payload.request.LoginRequest;
import com.bsep2024.MarketingAgency.payload.response.AccessTokenResponse;
import com.bsep2024.MarketingAgency.payload.response.MessageResponse;
import com.bsep2024.MarketingAgency.repository.RequestAdvertisementRepository;
import com.bsep2024.MarketingAgency.repository.UserRepository;
import com.bsep2024.MarketingAgency.security.services.UserDetailsImpl;
import com.bsep2024.MarketingAgency.services.RequestAdvertisementService;
import com.bsep2024.MarketingAgency.utils.AESUtil;
import com.bsep2024.MarketingAgency.utils.KeyStoreUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.security.KeyStore;
import java.util.Optional;



@RestController
@RequestMapping("/api/requestadvertisement")
public class RequestAdvertisementController {

    private final RequestAdvertisementService requestAdvertisementService;
    private static final Logger logger = LoggerFactory.getLogger(RequestAdvertisementController.class);

    @Autowired
    public RequestAdvertisementController(RequestAdvertisementService requestAdvertisementService) {
        this.requestAdvertisementService = requestAdvertisementService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('CREATE_ADVERTISEMENT_REQUEST')")
    public ResponseEntity<?> createRequestAdvertisement(@Valid @RequestBody RequestAdvertisement requestAdvertisement) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("CreateRequestAdvertisement request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                return this.requestAdvertisementService.createRequestAdvertisement(requestAdvertisement);

            }

            logger.warn("CreateRequestAdvertisement request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("CreateRequestAdvertisement request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("CreateRequestAdvertisement request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("CreateRequestAdvertisement request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('READ_OWN_REQUESTS')")
    public ResponseEntity<?> getRequestAvertisementByUserId(@Valid @PathVariable("userId") Long userId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetRequestAvertisementByUserId request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                return this.requestAdvertisementService.getRequestAvertisementByUserId(userId);
            }

            logger.warn("GetRequestAvertisementByUserId request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetRequestAvertisementByUserId request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetRequestAvertisementByUserId request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetRequestAvertisementByUserId request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_ALL_REQUESTS')")
    public ResponseEntity<?> getAll(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetAllRequestAdvertisements request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                return this.requestAdvertisementService.getAll();
            }

            logger.warn("GetAllRequestAdvertisements request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetAllRequestAdvertisements request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetAllRequestAdvertisements request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetAllRequestAdvertisements request made but was not executed, reason: " + e.getMessage()));
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ADVERTISEMENT_REQUEST')")
    public ResponseEntity<String> deleteRequestAdvertisement(@Valid @PathVariable("id") Long id) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("DeleteRequestAdvertisement request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                return this.requestAdvertisementService.deleteRequestAdvertisement(id);
            }

            logger.warn("DeleteRequestAdvertisement request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body("DeleteRequestAdvertisement request made but was not executed, authentication was not valid");
        }
        catch (Exception e){
            logger.error("DeleteRequestAdvertisement request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body("DeleteRequestAdvertisement request made but was not executed, reason: " + e.getMessage());
        }
    }
}
