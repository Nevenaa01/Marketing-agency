package com.bsep2024.MarketingAgency.services;

import com.bsep2024.MarketingAgency.models.EUserStatus;
import com.bsep2024.MarketingAgency.models.User;
import com.bsep2024.MarketingAgency.payload.request.DenialRequest;
import com.bsep2024.MarketingAgency.payload.response.MessageResponse;
import com.bsep2024.MarketingAgency.payload.response.UserInformation;
import com.bsep2024.MarketingAgency.repository.UserRepository;
import com.bsep2024.MarketingAgency.utils.AESUtil;
import com.bsep2024.MarketingAgency.utils.KeyStoreUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;
@Service
public class UserService {


    private final UserRepository userRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User denyUserRegistration(DenialRequest denialRequest){

        Optional<User> u = userRepository.findById(denialRequest.getUserId());

        if(u.isPresent()) {
            User user = u.get();
            user.setStatus(EUserStatus.REJECTED);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("markoradetic67@gmail.com");
            message.setSubject("Denied user");
            message.setText(denialRequest.getReport());

            javaMailSender.send(message);

            return userRepository.save(user);
        }
        return null;
    }


    public ResponseEntity<?> getUserInformationById(Long userId) {
        try {
            Optional<UserInformation> userInformationOptional = userRepository.findUserInformationById(userId);
            if (userInformationOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
            }

            UserInformation userInformation = userInformationOptional.get();
            SecretKey secretKey  = KeyStoreUtil.loadKey(userId.toString());

            // Decrypt fields
            String decryptedEmail = AESUtil.decrypt(userInformation.getEmail(), secretKey);
            String decryptedAddress = AESUtil.decrypt(userInformation.getAdress(), secretKey);
            String decryptedPhoneNumber = AESUtil.decrypt(userInformation.getPhoneNumber(), secretKey);
            String decryptedCity = AESUtil.decrypt(userInformation.getCity(), secretKey);

            // Create a DTO to return decrypted information
            UserInformation userInformationDTO = new UserInformation(
                    userInformation.getId(),
                    userInformation.getName(),
                    decryptedEmail,
                    userInformation.getSurname(),
                    decryptedAddress,
                    decryptedCity,
                    userInformation.getCountry(),
                    decryptedPhoneNumber,
                    userInformation.getPackageType()
            );

            return ResponseEntity.ok(userInformationDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error: Unable to decrypt data."));
        }
    }

    public ResponseEntity<?> updateUserInfromation(UserInformation updatedUser) {
        try {
            SecretKey secretKey  = KeyStoreUtil.loadKey(updatedUser.getId().toString());
            int updatedRows = userRepository.updateUserInformation(updatedUser.getId(), updatedUser.getName(), updatedUser.getSurname(),
                    AESUtil.encrypt(updatedUser.getPhoneNumber(), secretKey),
                    AESUtil.encrypt(updatedUser.getEmail(), secretKey),
                    AESUtil.encrypt(updatedUser.getAdress(), secretKey),
                    AESUtil.encrypt(updatedUser.getCity(), secretKey),
                    updatedUser.getCountry());
            if (updatedRows > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
