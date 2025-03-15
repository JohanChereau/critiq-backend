package com.johanchereau.critiqbackend.auth;

import com.johanchereau.critiqbackend.email.EmailService;
import com.johanchereau.critiqbackend.email.EmailTemplateName;
import com.johanchereau.critiqbackend.security.JwtService;
import com.johanchereau.critiqbackend.user.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();

        claims.put("fullname", user.getFullName());
        claims.put("username", user.getUsername());

        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

//    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                //TODO: better error handling
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
