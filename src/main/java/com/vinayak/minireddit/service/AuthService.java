package com.vinayak.minireddit.service;

import com.vinayak.minireddit.dto.AuthenticationResponse;
import com.vinayak.minireddit.dto.LoginRequest;
import com.vinayak.minireddit.dto.RegisterRequest;
import com.vinayak.minireddit.exceptions.SpringRedditException;
import com.vinayak.minireddit.model.NotificationEmail;
import com.vinayak.minireddit.model.User;
import com.vinayak.minireddit.model.VerificationToken;
import com.vinayak.minireddit.repository.UserRepository;
import com.vinayak.minireddit.repository.VerificationTokenRepository;
import com.vinayak.minireddit.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(RegisterRequest registerRequest){

        User user=new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token=generateVerificationToken(user);

        mailService.sendEmail(new NotificationEmail("Please activate your account",user.getEmail(),
                                                     "Thank you for signing up to Spring Reddit, "+
                                                     "Please click on the below url to activate your account: "+
                                                     "http://localhost:8080/api/auth/accountVerification/"+token));
    }

    private String generateVerificationToken(User user) {

        String token= UUID.randomUUID().toString();
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken=verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username=verificationToken.getUser().getUserName();
        User user=userRepository.findByUserName(username).orElseThrow(()->new SpringRedditException("User not found with name -"+username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                           loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token=jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token,loginRequest.getUsername());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUserName(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }
}
