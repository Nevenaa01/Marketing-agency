package com.bsep2024.MarketingAgency.controllers;

import com.bsep2024.MarketingAgency.models.*;
import com.bsep2024.MarketingAgency.dto.EmployeeDto;
import com.bsep2024.MarketingAgency.payload.request.DenialRequest;
import com.bsep2024.MarketingAgency.payload.request.LoginRequest;
import com.bsep2024.MarketingAgency.payload.response.MessageResponse;
import com.bsep2024.MarketingAgency.repository.AdRepository;
import com.bsep2024.MarketingAgency.repository.RequestAdvertisementRepository;
import com.bsep2024.MarketingAgency.security.services.UserDetailsImpl;
import com.bsep2024.MarketingAgency.services.ActivationLinkService;
import com.bsep2024.MarketingAgency.services.UserService;
import com.bsep2024.MarketingAgency.payload.response.UserInformation;
import com.bsep2024.MarketingAgency.utils.AESUtil;
import com.bsep2024.MarketingAgency.utils.EmailHashUtil;
import com.bsep2024.MarketingAgency.utils.KeyStoreUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import com.bsep2024.MarketingAgency.payload.request.SignupRequest;
import com.bsep2024.MarketingAgency.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserRepository userRepository;
    private final RequestAdvertisementRepository requestAdvertisementRepository;
    private final AdRepository adRepository;
    private final UserService userService;
    private final ActivationLinkService activationLinkService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private JavaMailSender javaMailSender;
    public UserController(UserRepository userRepository,  UserService userService, ActivationLinkService activationLinkService, RequestAdvertisementRepository requestAdvertisementRepository,
                          AdRepository adRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.activationLinkService = activationLinkService;
        this.requestAdvertisementRepository = requestAdvertisementRepository;
        this.adRepository = adRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_ALL_USERS')")
    public List<User> getAll() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey1 = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetAllUsers request made by " + AESUtil.decrypt(userDetails.getEmail(),secretKey1));

                List<User> users =  userRepository.findAll();
                for (User user : users) {
                    SecretKey secretKey = KeyStoreUtil.loadKey(user.getId().toString());
                    user.decryptFields(secretKey);

                }
                return users;
            }

            logger.warn("GetAllUsers request made but was not executed, authentication was not valid");
            return null;
        }
        catch (Exception e){
            logger.error("GetAllUsers request made but was not executed, reason: " + e.getMessage());
            return null;
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('READ_PENDING_USERS')")
    public ResponseEntity<?> getPendingUsers() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKeyy = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetPendingUsers request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKeyy));

                List<User> pendingUsers = userRepository.findAll();

                for (User user : pendingUsers) {
                    SecretKey secretKey = KeyStoreUtil.loadKey(user.getId().toString());
                    user.decryptFields(secretKey);
                }

                return ResponseEntity.ok(pendingUsers);
            }

            logger.warn("GetPendingUsers request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetPendingUsers request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetAllUsers request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetAllUsers request made but was not executed, reason: " + e.getMessage()));
        }

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_USER_BY_ID')")
    public ResponseEntity<?> getUserById(@Valid @PathVariable("id") Long id){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKeyy = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetUserById request made by " + AESUtil.decrypt(userDetails.getEmail(),secretKeyy));

                Optional<User> u = userRepository.findById(id);
                if(u.isEmpty()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
                }

                User user = u.get();

                SecretKey secretKey = KeyStoreUtil.loadKey(user.getId().toString());
                user.decryptFields(secretKey);

                return ResponseEntity.ok(user);
            }

            logger.warn("GetUserById request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetUserById request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetUserById request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetUserById request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKeyy = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("UpdateUser request made by " + AESUtil.decrypt(userDetails.getEmail(),secretKeyy));

                Optional<User> u = userRepository.findById(user.getId());
                if(u.isEmpty()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
                }

                SecretKey secretKey = KeyStoreUtil.loadKey(user.getId().toString());
                user.setEmailHash(EmailHashUtil.hashEmail(user.getEmail()));
                user.encryptFields(secretKey);
                user.setVerified(true);
                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(updatedUser);
            }

            logger.warn("UpdateUser request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("UpdateUser request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("UpdateUser request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("UpdateUser request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @PostMapping("/deny")
    @PreAuthorize("hasAuthority('DENY_USERREGISTRATION')")
    public ResponseEntity<?> denyUserRegistration(@RequestBody DenialRequest denialRequest){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKeyy = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("DenyUserRegistration request made by " + AESUtil.decrypt(userDetails.getEmail(),secretKeyy));
                return ResponseEntity.ok(userService.denyUserRegistration(denialRequest));
            }

            logger.warn("DenyUserRegistration request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("DenyUserRegistration request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("DenyUserRegistration request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("DenyUserRegistration request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKeyy = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("DeleteUser request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKeyy));
                userRepository.deleteById(id);
                return ResponseEntity.ok(new MessageResponse("Successfully removed user!"));
            }

            logger.warn("DeleteUser request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("DeleteUser request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("DeleteUser request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("DeleteUser request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete_all_my_data/{userId}")
    @PreAuthorize("hasAuthority('DELETE_ALL_MY_DATA')")
    @Transactional
    public ResponseEntity<?> deleteAllMyData(@PathVariable("userId") Long userId){
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("User not found with ID: " + userId));
            }

            User user = userOptional.get();

            if (user.getPackageType() != EPackage.GOLDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("User is not a gold member. Unable to delete data."));
            }

            Set<Role> roles = user.getRoles();
            boolean isModerator = roles.stream().anyMatch(role -> role.getName().equals(ERole.ROLE_MODERATOR));
            if (isModerator) {
                adRepository.deleteByEmployeeId(userId);
            } else {
                requestAdvertisementRepository.deleteByUserId(userId);
                adRepository.deleteByClientId(userId);
            }


            userRepository.deleteById(userId);

            return ResponseEntity.ok(new MessageResponse("Successfully removed user and associated data."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to delete a user!: " + e.getMessage());
        }
    }


    @PutMapping("/employee")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> updateEmploye(@Valid @RequestBody UserInformation user){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKeyy = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("UpdateEmploye request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKeyy));

                SecretKey secretKey  = KeyStoreUtil.loadKey(user.getId().toString());
                User userForUpdate = userRepository.findById(user.getId()).get();

                userForUpdate.setName(user.getName());
                userForUpdate.setSurname(user.getSurname());
                userForUpdate.setPhoneNumber(AESUtil.encrypt(user.getPhoneNumber(),secretKey));
                userForUpdate.setCity(AESUtil.encrypt(user.getCity(),secretKey));
                userForUpdate.setCountry(user.getCountry());
                userForUpdate.setAdress(AESUtil.encrypt(user.getAdress(),secretKey));

                User updatedUser = userRepository.save(userForUpdate);

                return ResponseEntity.ok(user);
            }

            logger.warn("UpdateEmploye request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("UpdateEmploye request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("UpdateEmploye request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("UpdateEmploye request made but was not executed, reason: " + e.getMessage()));
        }

    }

    @GetMapping("/personal-info/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> getUserInformationById(@Valid @PathVariable("userId") Long userId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GetUserInformationById request made by " + AESUtil.decrypt(userDetails.getEmail(),secretKey));
                return this.userService.getUserInformationById(userId);
            }

            logger.warn("GetUserInformationById request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("GetUserInformationById request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("GetUserInformationById request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("GetUserInformationById request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @PutMapping("/update-user")
    @PreAuthorize("hasAuthority('UPDATE_USER_PROFILE')")
    public ResponseEntity<?> updateUserInfromation(@Valid @RequestBody UserInformation updatedUser) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("UpdateUserInfromation request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));
                return this.userService.updateUserInfromation(updatedUser);
            }

            logger.warn("UpdateUserInfromation request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("UpdateUserInfromation request made but was not executed, authentication was not valid"));
        }
        catch (Exception e){
            logger.error("UpdateUserInfromation request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("UpdateUserInfromation request made but was not executed, reason: " + e.getMessage()));
        }
    }

    @GetMapping("/generateActivationLink/{userId}/{userEmail}")
    @PreAuthorize("hasAuthority('GENERATE_ACTIVATION_LINK')")
    public ResponseEntity<String> generateActivationLink(@PathVariable("userId") Long userId, @PathVariable("userEmail") String userEmail) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("GenerateActivationLink request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));
                String activationLink = activationLinkService.generateActivationLink(userId);
                sendActivationEmail(userEmail, activationLink);
                return ResponseEntity.ok("Activation link sent to user's email");
            }

            logger.warn("GenerateActivationLink request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body("GenerateActivationLink request made but was not executed, authentication was not valid");
        }
        catch (Exception e){
            logger.error("GenerateActivationLink request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body("GenerateActivationLink request made but was not executed, reason: " + e.getMessage());
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam Long userId,
                                               @RequestParam String token,
                                               @RequestParam long expirationDate,
                                               @RequestParam String hmac) {

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("ActivateUser request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                boolean isValid = activationLinkService.verifyActivationLink(userId, token, expirationDate, hmac);
                if (isValid) {
                    User u = userRepository.findById(userId).get();

                    u.setVerified(true);
                    u.setStatus(EUserStatus.ACTIVE);
                    userRepository.save(u);

                    return ResponseEntity.ok("User activated successfully");
                } else {
                    logger.info("ActivateUser request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey) + " but invalid activation link was used");
                    return ResponseEntity.badRequest().body("Invalid activation link");
                }
            }

            logger.warn("ActivateUser request made but was not executed, authentication was not valid");
            return ResponseEntity.badRequest().body("ActivateUser request made but was not executed, authentication was not valid");
        }
        catch (Exception e){
            logger.error("ActivateUser request made but was not executed, reason: " + e.getMessage());
            return ResponseEntity.badRequest().body("ActivateUser request made but was not executed, reason: " + e.getMessage());
        }
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public void sendMail() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("SendMail request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo("jovan.kataniclol@gmail.com");
                message.setSubject("Paswordless login");
                message.setText("Dear " + ",\n\n"
                        + "Thank you for joining us. Please click on the following link to verify your account: "
                        + "http://localhost:8090/api/v1/user/verify/" );

                javaMailSender.send(message);
            }

            logger.warn("SendMail request made but was not executed, authentication was not valid");
        }
        catch (Exception e){
            logger.error("SendMail request made but was not executed, reason: " + e.getMessage());
        }
    }

    private void sendActivationEmail(String userEmail, String activationLink) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
                logger.info("SendActivationEmail request made by " + AESUtil.decrypt(userDetails.getEmail(), secretKey));

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo("markoradetic67@gmail.com");
                mailMessage.setSubject("Account Activation Link");
                mailMessage.setText("Please use the following link to activate your account: " + activationLink);
                javaMailSender.send(mailMessage);
            }

            logger.warn("SendActivationEmail request made but was not executed, authentication was not valid");
        }
        catch (Exception e){
            logger.error("SendActivationEmail request made but was not executed, reason: " + e.getMessage());
        }
    }

    @GetMapping("/vpn")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    private ResponseEntity<MessageResponse> getMessageVPN(){
        String vpnEndpoint = "http://10.13.13.1:3000";
        RestTemplate restTemplate = new RestTemplate();

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());

                logger.info("User " + AESUtil.decrypt(userDetails.getEmail(), secretKey) + " accessed endpoint for testing vpn..");
                String response = restTemplate.getForObject(vpnEndpoint, String.class);
                return ResponseEntity.ok(new MessageResponse(response));
            }

            logger.warn("Accessing VPN endpoint failed, authentication wasn't valid");
            return ResponseEntity.badRequest().body(new MessageResponse("Accessing VPN endpoint failed, authentication wasn't valid"));
        }
        catch (Exception e){
            logger.error("Accessing VPN endpoint failed, reason: ", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
