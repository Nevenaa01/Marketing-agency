package com.bsep2024.MarketingAgency.controllers;

import com.bsep2024.MarketingAgency.models.Ad;
import com.bsep2024.MarketingAgency.payload.response.MessageResponse;
import com.bsep2024.MarketingAgency.security.services.UserDetailsImpl;
import com.bsep2024.MarketingAgency.utils.AESUtil;
import com.bsep2024.MarketingAgency.utils.KeyStoreUtil;
import com.bsep2024.MarketingAgency.payload.request.RateLimitingRequest;
import com.bsep2024.MarketingAgency.security.ratelimit.WithRateLimitProtection;
import com.bsep2024.MarketingAgency.services.AdService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;

@RestController
@RequestMapping("/api/ad")
public class AdController {
    @Autowired
    private AdService _adService;
    private static final Logger logger = LoggerFactory.getLogger(AdController.class);


    @GetMapping("/findAllByEmployeeId/{id}")
    @PreAuthorize("hasAuthority('READ_OWN_ADVERTISEMENT')")
    public ResponseEntity<?> findAllByEmployeeId(@PathVariable("id") Long id){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("FindAllAdsByEmployeeId request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                List<Ad> ads = _adService.findAllByEmployeeId(id);

                return ResponseEntity.ok(ads);
            }

            logger.warn("FindAllAdsByEmployeeId request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("FindAllAdsByEmployeeId request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("FindAllAdsByEmployeeId request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("FindAllAdsByEmployeeId request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @GetMapping("/findByRequestId/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> getByRequestId(@Valid @PathVariable Long id){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetAdByRequestId request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                return ResponseEntity.ok(_adService.findByRequestId(id));
            }

            logger.warn("GetAdByRequestId request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetAdByRequestId request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetAdByRequestId request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetAdByRequestId request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ADVERTISEMENT')")
    public ResponseEntity<?> create(@Valid @RequestBody Ad ad){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("CreateAd request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                Ad savedAd = _adService.create(ad);

                return ResponseEntity.ok(savedAd);
            }

            logger.warn("CreateAd request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("CreateAd request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("CreateAd request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("CreateAd request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @GetMapping("/findByClientId/{clientId}")
    @PreAuthorize("hasAuthority('READ_ADVERTISEMENT_FOR_YOU')")
    public ResponseEntity<?> getAdvertisementByClientId(@Valid @PathVariable Long clientId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetAdvertisementByClientId request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                return ResponseEntity.ok(_adService.findAllByClientId(clientId));
            }

            logger.warn("GetAdvertisementByClientId request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetAdvertisementByClientId request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetAdvertisementByClientId request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetAdvertisementByClientId request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @PostMapping("/testRateLimiting")
    @WithRateLimitProtection
    public ResponseEntity<?> testRateLimiting( @RequestBody RateLimitingRequest rateLimitingRequest){
        return ResponseEntity.ok(_adService.getAll());
    }

}
