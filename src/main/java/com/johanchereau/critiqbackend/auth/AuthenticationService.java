package com.johanchereau.critiqbackend.auth;

import com.johanchereau.critiqbackend.email.EmailService;
import com.johanchereau.critiqbackend.email.EmailTemplateName;
import com.johanchereau.critiqbackend.user.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.mailing.auth.register.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest registrationRequest) throws MessagingException {
        var userRole = userRoleRepository.findByName("USER")
                //TODO: better exception handling
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        var user = User.builder()
                .firstname(registrationRequest.getFirstname())
                .lastname(registrationRequest.getLastname())
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .dateOfBirth(registrationRequest.getDateOfBirth())
                .termsAccepted(registrationRequest.getTermsAccepted())
                .termsAcceptedAt(LocalDateTime.now())
                .newsletterAccepted(registrationRequest.getNewsletterAccepted())
                .newsletterAcceptedAt(LocalDateTime.now())
                .accountLocked(false)
                .enabled(false)
                .userRoles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName() + " (" + user.getUsername() + ")",
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Activate your account"
        );

    }

    private String generateAndSaveActivationToken(User user) {
        //Generate a token
        String generatedToken = generateActivationCode(6);

        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder activationCodeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // 0...9
            activationCodeBuilder.append(characters.charAt(randomIndex));
        }

        return activationCodeBuilder.toString();
    }
}
