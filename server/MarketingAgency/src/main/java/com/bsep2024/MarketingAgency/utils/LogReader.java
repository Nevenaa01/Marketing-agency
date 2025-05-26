package com.bsep2024.MarketingAgency.utils;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.bsep2024.MarketingAgency.notifications.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import org.springframework.stereotype.Component;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogReader extends AppenderBase<ILoggingEvent>{
    private static Map<String,Integer> loginAttemptsMap= new HashMap<>();
    private final String loginFailedRegex = "User with email .+ could not login, reason: Bad credentials";
    private static LocalDateTime dateTime=LocalDateTime.now();
    public final int numberOfLoginsAllowed = 10;
    private static JavaMailSender javaMailSender;
    private static NotificationService notif;

    public void setService(NotificationService notif){
        this.notif=notif;
    }
    @Override
    public void start() {
        super.start();
        setUpMail();
    }
    private void setUpMail(){
        if(javaMailSender==null){
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername("markoradetic67@gmail.com");
            mailSender.setPassword("zjhr jitq xent roob");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            javaMailSender=mailSender;
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        long diffInMilliseconds = Duration.between(dateTime, LocalDateTime.now()).toMillis();
        if(diffInMilliseconds>=86400000){
            dateTime=LocalDateTime.now();
            loginAttemptsMap.clear();
        }
        checkNumberOfLogins(formattedMessage);
    }

    private void checkNumberOfLogins(String formattedMessage){
        Pattern pattern = Pattern.compile(loginFailedRegex);
        Matcher matcher =pattern.matcher(formattedMessage);
        if(matcher.matches()){
            String[] words = formattedMessage.split(" ");
            String userEmail=words[3];
            Integer numCalled=loginAttemptsMap.getOrDefault(userEmail, 0);
            loginAttemptsMap.put(userEmail,numCalled+1);
            if(numCalled>=numberOfLoginsAllowed){
                loginAttemptsMap.put(userEmail,0);
                String text="The user "+userEmail+" has tried to log in more than "+numberOfLoginsAllowed+" times this day!";

                notif.sendNotification(text);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo("jovan.kataniclol@gmail.com");
                message.setSubject("Login warning from "+userEmail);
                message.setText(text);
                javaMailSender.send(message);
            }
        }
    }
}
