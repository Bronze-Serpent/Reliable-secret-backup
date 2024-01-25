package com.barabanov.backup.cryptography;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Configuration
public class CryptographyConfig
{

    @Bean
    public MessageDigest messageDigest(@Value("${crypto.hash.algorithm:MD5}") String hashAlgorithm)
            throws NoSuchAlgorithmException
    {
        return MessageDigest.getInstance(hashAlgorithm);
    }

    @Bean
    public SecretKeyFactory secretKeyFactory(@Value("${crypto.pas.to.key.algorithm:PBKDF2WithHmacSHA1}") String pswdToKeyAlgorithm)
            throws NoSuchAlgorithmException
    {
        return SecretKeyFactory.getInstance(pswdToKeyAlgorithm);
    }
}
