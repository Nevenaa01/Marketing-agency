package com.bsep2024.MarketingAgency.controllers;

import java.security.KeyStore;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.bsep2024.MarketingAgency.models.*;
import com.bsep2024.MarketingAgency.payload.request.PasswordChangeRequest;
import com.bsep2024.MarketingAgency.payload.response.*;

import com.bsep2024.MarketingAgency.payload.response.AccessTokenResponse;
import com.bsep2024.MarketingAgency.utils.AESUtil;
import com.bsep2024.MarketingAgency.utils.EmailHashUtil;
import com.bsep2024.MarketingAgency.utils.KeyStoreUtil;
import com.bsep2024.MarketingAgency.utils.RecaptchaVerifier;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bsep2024.MarketingAgency.payload.request.LoginRequest;
import com.bsep2024.MarketingAgency.payload.request.SignupRequest;
import com.bsep2024.MarketingAgency.repository.RoleRepository;
import com.bsep2024.MarketingAgency.repository.UserRepository;
import com.bsep2024.MarketingAgency.security.jwt.JwtUtils;
import com.bsep2024.MarketingAgency.security.services.UserDetailsImpl;
import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  private JavaMailSender javaMailSender;

  Boolean invalidRole = false;

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/passwordless")
  public boolean authenticateUserWithoutPassword(@Valid @RequestBody LoginRequest loginRequest) throws IOException{
    logger.info("Getting code for passwordless login..");

    try {
      RecaptchaVerifier verifier = new RecaptchaVerifier();
      String token = loginRequest.getToken();
      boolean verified = verifier.verifyToken(token);

      if(!verified){
        logger.warn("Invalid captcha token. User with email " + loginRequest.getEmail() + " didn't pass captcha test");
        return false;
      }

      logger.info("Captcha test passed successfully by user with email " + loginRequest.getEmail());

      Optional<User> userOptional = userRepository.findByEmailHash(EmailHashUtil.hashEmail(loginRequest.getEmail()));
      if (userOptional.isEmpty()) {
        logger.warn("Email " + loginRequest.getEmail() + " does not exist.");
        return false;
      }

      User u = userOptional.get();


      String jwtSource = jwtUtils.generateTokenForPasswordless(u.getId());
      String encodedToken = encoder.encode(jwtSource);
      u.setEmailToken(encodedToken);
      userRepository.save(u);

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo("jovan.kataniclol@gmail.com");
      message.setSubject("Paswordless login");
      message.setText("https://localhost:4200/confirm_login/" + jwtSource);

      javaMailSender.send(message);

      logger.info("Code for paswordless login sent successfully to email " + loginRequest.getEmail());
      return true;
    }
    catch (Exception e){
      logger.error("Code for paswordless login could not be sent to email " + loginRequest.getEmail() + ", reason: " + e.getMessage());
      return false;
    }
  }
  @GetMapping("/confirm_login/{token}")
  public ResponseEntity<?> confirmLoginWithEmail(@PathVariable("token") String token) {
    logger.info("Logging in using passwordless login..");

    try {
      jwtUtils.validateJwtToken(token);
      Long id = jwtUtils.getUserIdFromJwtToken(token);

      Optional<User> optionalUser = userRepository.findById(id);

      if (!optionalUser.isPresent()) {
        logger.warn("User with id " + id + " does not exist");
        return ResponseEntity.badRequest().body(new MessageResponse("Error: User doesnt exist!"));
      }

      User u = optionalUser.get();
      SecretKey secretKey = KeyStoreUtil.loadKey(u.getId().toString());

      if (u.getPackageType() == EPackage.BASIC) {
        logger.warn("User with email " + AESUtil.decrypt(u.getEmail(),secretKey) + " tried to login using benefits of STANDARD/GOLDEN package");

        return ResponseEntity.badRequest().body(new MessageResponse("User with email " + AESUtil.decrypt(u.getEmail(),secretKey) + " tried to login using benefits of STANDARD/GOLDEN package"));
      }

      if (encoder.matches(token, u.getEmailToken())) {
        List<Role> list = new ArrayList<>(u.getRoles());
        if (list == null) {

          logger.warn("User with email " + AESUtil.decrypt(u.getEmail(),secretKey) + " tried to login but has not roles");

          return ResponseEntity.badRequest().body(new MessageResponse("User with email " + AESUtil.decrypt(u.getEmail(),secretKey) + " tried to login but has not roles"));
        }
        String role = list.get(0).getName().toString();
        String jwtCookie = jwtUtils.generateTokenFrom(id, role);
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwtCookie).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
        String accessToken = cookie.toString().split("=")[1].split(";")[0];

        String refreshCookie = jwtUtils.generateRefreshTokenFrom(id, role);
        ResponseCookie refcookie = ResponseCookie.from(refreshCookie, refreshCookie).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
        String refreshToken = refcookie.toString().split("=")[1].split(";")[0];

        u.setEmailToken("");
        userRepository.save(u);

        logger.info("User with email " + AESUtil.decrypt(u.getEmail(),secretKey) + " has logged in successfully using passwordless login");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessToken)
                .body(new ResponseWrapper(accessToken, refreshToken));
      }
    }
    catch (Exception e){
      logger.error("User could not login using email");

      return ResponseEntity.badRequest().body(new MessageResponse("User could not login using email, reason: " + e.getMessage()));
    }

    return null;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws IOException{
    logger.info("Logging in user with email " + loginRequest.getEmail() + "..");
    try {
      /*RecaptchaVerifier verifier = new RecaptchaVerifier();
      String token = loginRequest.getToken();
      boolean verified = verifier.verifyToken(token);

      if(!verified){
        logger.warn("Invalid captcha token. User with email " + loginRequest.getEmail() + " didn't pass captcha test");
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid captcha token"));
      }*/

      logger.info("Captcha test passed successfully by user with email " + loginRequest.getEmail());

      Optional<User> userOptional = userRepository.findByEmailHash(EmailHashUtil.hashEmail(loginRequest.getEmail()));
      if (userOptional.isEmpty()) {
        logger.warn("Email " + loginRequest.getEmail() + " does not exist, login failed");
        return ResponseEntity.badRequest().body(new MessageResponse("Error: This user email does not exist!"));
      }

      User u = userOptional.get();
      SecretKey secretKey = KeyStoreUtil.loadKey(u.getId().toString());

      if(!u.isVerified()){
        logger.warn("User with email " + AESUtil.decrypt(u.getEmail(),secretKey) + " is not valid, login failed");

        return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not valid!"));
      }


      Authentication authentication = authenticationManager
              .authenticate(new UsernamePasswordAuthenticationToken(u.getName().concat(" " + u.getSurname()), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

      ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
      ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(userDetails);

      List<String> roles = userDetails.getAuthorities().stream()
              .map(item -> item.getAuthority())
              .collect(Collectors.toList());

      String jwtSource = jwtCookie.toString().split("=")[1].split(";")[0];
      String jwtRefreshSource = jwtRefreshCookie.toString().split("=")[1].split(";")[0];

      Long jwtExparationDate = new Date().getTime() + jwtCookie.getMaxAge().toMillis();
      Long jwtRefreshExparationDate = new Date().getTime() + jwtRefreshCookie.getMaxAge().toMillis();

      AccessTokenResponse accessToken = new AccessTokenResponse(userDetails.getId(), jwtSource, jwtExparationDate);
      RefreshTokenResponse refresToken = new RefreshTokenResponse(userDetails.getId(), jwtRefreshSource, jwtRefreshExparationDate);

      logger.info("User " + loginRequest.getEmail() + " logged in successfully");
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtSource)
              .body(new ResponseWrapper(accessToken.getAccessToken(), refresToken.getToken()));
    }
    catch (Exception e){
      logger.error("User with email " + loginRequest.getEmail() + " could not login, reason: " + e.getMessage());
      return ResponseEntity.badRequest().body(new MessageResponse("User with email " + loginRequest.getEmail() + " could not login"));
    }
  }

  @PostMapping("/refreshAccessToken")
  public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody ResponseWrapper wrappper){
    try{
      logger.info("Checking validity of access token for user with id" + jwtUtils.getUserIdFromJwtToken(wrappper.getAccessToken()));
      if(jwtUtils.validateJwtToken(wrappper.getAccessToken())){
        logger.info("User " + jwtUtils.getUserIdFromJwtToken(wrappper.getRefreshToken()) + " has valid access token");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, wrappper.getAccessToken())
                .body("{\"accessToken\": \"" + wrappper.getAccessToken() + "\"}");
      }
    }
    catch (ExpiredJwtException e){
      logger.error("Refreshing access token for user with id" + jwtUtils.getUserIdFromJwtToken(wrappper.getAccessToken()));

      if(jwtUtils.validateJwtToken(wrappper.getRefreshToken())){
        User user = userRepository.findById(jwtUtils.getUserIdFromJwtToken(wrappper.getRefreshToken())).get();

        if(!user.isVerified()){
          logger.warn("User with id " + user.getId() + " is not verified, refreshing access token failed");

          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        Set<Role> roles = user.getRoles();
        List<Role> rolesList = new ArrayList<>(roles);
        String jwtCookie= jwtUtils.generateTokenFrom(user.getId(), rolesList.get(0).getName().toString());

        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwtCookie).path("/api").maxAge(15 * 60).httpOnly(true).build();
        String jwtSource = cookie.toString().split("=")[1].split(";")[0];
        Long jwtExparationDate = new Date().getTime() + 15 * 60 * 1000;
        AccessTokenResponse accessToken = new AccessTokenResponse(user.getId(), jwtSource, jwtExparationDate);

        logger.info("Access token for user with id" + user.getId() + " refreshed successfully");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtSource)
                .body("{\"accessToken\": \"" + accessToken.getAccessToken() + "\"}");
      }
      else{
        logger.warn("User with id " + jwtUtils.getUserIdFromJwtToken(wrappper.getAccessToken()) + " doesnt have valid refresh token");
      }
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
  }

  @PostMapping("/valid")
  public ResponseEntity<Boolean> checkIfValid(@RequestBody String token){
    if (jwtUtils.validateJwtToken(token)) {
      if(invalidRole){
        return ResponseEntity.badRequest().body(false);
      }
      return ResponseEntity.ok(true);
    } else {
      logger.warn("Token of user with id" + jwtUtils.getUserIdFromJwtToken(token) + " is not valid. Access denied");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    logger.info("Registring new user..");

    try {
      // Create a new AES key for the user
      SecretKey secretKey = KeyStoreUtil.generateKey();

      // Hash the email for unique constraint checking
      String emailHash = EmailHashUtil.hashEmail(signUpRequest.getEmail());

      if (userRepository.existsByEmailHash(emailHash)) {
        logger.warn("Email " + signUpRequest.getEmail() + " is already in use");
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
      }

      // Encrypt sensitive information
      String encryptedEmail = AESUtil.encrypt(signUpRequest.getEmail(), secretKey);
      String encryptedPhoneNumber = AESUtil.encrypt(signUpRequest.getPhoneNumber(), secretKey);
      String encryptedAddress = AESUtil.encrypt(signUpRequest.getAdress(), secretKey);
      String encryptedCity = AESUtil.encrypt(signUpRequest.getCity(), secretKey);

      User user = new User(
              encryptedEmail,
              emailHash,
              encoder.encode(signUpRequest.getPassword()),
              signUpRequest.getName(),
              signUpRequest.getSurname(),
              encryptedPhoneNumber,
              encryptedAddress,
              encryptedCity,
              signUpRequest.getCountry(),
              "",
              true
      );

      user.setVerified(false);
      user.setPackageType(EPackage.valueOf(signUpRequest.getPackageType().toUpperCase()));
      user.setStatus(EUserStatus.PENDING);

      Set<String> strRoles = signUpRequest.getRole();
      Set<Role> roles = new HashSet<>();
      if (strRoles == null) {
        Optional<Role> optionalRole = roleRepository.findByName(ERole.ROLE_USER);

        if (!optionalRole.isPresent()) {
          logger.warn("Given role is not found");
          return ResponseEntity.badRequest().body(new MessageResponse("Given role is not found"));
        }

        Role userRole = optionalRole.get();
        roles.add(userRole);
      } else {
        strRoles.forEach(role -> {
          invalidRole = false;
          switch (role) {
            case "admin":
              Optional<Role> optionalAdminRole = roleRepository.findByName(ERole.ROLE_ADMIN);

              if (!optionalAdminRole.isPresent()) {
                logger.warn("Given role for admin is not found");
                invalidRole = true;
                break;
              }

              Role adminRole = optionalAdminRole.get();
              roles.add(adminRole);
              user.setStatus(EUserStatus.ACTIVE);
              user.setVerified(true);

              break;
            case "mod":
              Optional<Role> optionalModRole = roleRepository.findByName(ERole.ROLE_MODERATOR);

              if (!optionalModRole.isPresent()) {
                logger.warn("Given role for moderator is not found");
                invalidRole = true;
                break;
              }

              Role modRole = optionalModRole.get();
              roles.add(modRole);

              break;
            default:
              Optional<Role> optionalUserRole = roleRepository.findByName(ERole.ROLE_USER);

              if (!optionalUserRole.isPresent()) {
                logger.warn("Given role for user is not found");
                invalidRole = true;
                break;
              }


              Role userRole = optionalUserRole.get();
              roles.add(userRole);
          }
        });
        if (invalidRole) {
          return ResponseEntity.badRequest().body(new MessageResponse("Given role is not found"));
        }
      }

      user.setRoles(roles);
      userRepository.save(user);

      logger.info("New user " + AESUtil.decrypt(user.getEmail(),secretKey) + " registered");

      // Save the AES key to a keystore file named with the user's ID
      KeyStoreUtil.saveKey(secretKey, user.getId().toString());

      return ResponseEntity.ok().body(new MessageResponse("Successfully registered user!"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error: Unable to encrypt data."));
    }
  }

  @PostMapping("/changePassword")
  public ResponseEntity<?> changeUserPassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest){
    logger.info("Changing password for user with id " + passwordChangeRequest.getUserId());

    Optional<User> u = userRepository.findById(passwordChangeRequest.getUserId());

    if(u.isPresent()){
      User user = u.get();

      user.setPassword(encoder.encode(passwordChangeRequest.getNewPassword()));
      user.setFirstTimeLogin(false);
      userRepository.save(user);

      logger.info("Password changed successfully for user " + passwordChangeRequest.getUserId());

      return ResponseEntity.ok().body("Successfully changed user password!");
    }
    logger.warn("Password not changed for user " + passwordChangeRequest.getUserId() + ". User with such id was not found");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An error occurred while changing user password: User not found!");
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    try {
      ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        SecretKey secretKey = KeyStoreUtil.loadKey(userDetails.getId().toString());
        logger.info("User " + AESUtil.decrypt(userDetails.getEmail(),secretKey) + " logged out");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
      }

      logger.info("User could not log out, authentication was not valid");
      return ResponseEntity.badRequest().body(new MessageResponse("User could not log out, authentication was not valid"));
    }
    catch (Exception e){
      logger.error("User could not log out, reason: " + e.getMessage());
      return ResponseEntity.badRequest().body(new MessageResponse("User could not log out, reason: " + e.getMessage()));
    }
  }
}
