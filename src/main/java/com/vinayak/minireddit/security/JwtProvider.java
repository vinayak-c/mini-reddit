package com.vinayak.minireddit.security;

import com.vinayak.minireddit.exceptions.SpringRedditException;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init(){
        try{
            keyStore=keyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/minireddit.jks");
            keyStore.load(resourceAsStream,"minisecret".toCharArray());
        }catch (KeyStoreException| CertificateException| NoSuchAlgorithmException| IOException e){
            throw new SpringRedditException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication){
        User principal=(User)authentication.getPrincipal();
        return  Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey(){
        try{
            return  (PrivateKey)keyStore.getKey("minireddit","minisecret".toCharArray());
        }catch (KeyStoreException|NoSuchAlgorithmException|UnrecoverableEntryException e){
            throw new SpringRedditException("Exception occurred while retrieving public key form keystore");
        }
    }

    public boolean validateToken(String jwt){
        Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try{
            return keyStore.getCertificate("minireddit").getPublicKey();
        } catch (KeyStoreException e){
            throw new SpringRedditException("Exception occurred while retrieving public key");
        }
    }

    public String getUsernameFromJwt(String token){
        Claims claims=Jwts.parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
