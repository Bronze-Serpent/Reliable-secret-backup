package com.barabanov.backup.cryptography;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@RequiredArgsConstructor
@Service
public class DefaultCryptoProviderService implements CryptoService
{

    private final MessageDigest messageDigest;
    private final SecretKeyFactory secretKeyFactory;

    @Value("${crypto.encode.algorithm:Blowfish}")
    private final String encodeAlgorithm;

    @Value("${crypto.pas.iterations:10}")
    private final int iterations;

    @Value("${crypto.key.length:32}")
    private final int keyLength;

    @Value("${crypto.pas.salt:asdf}")
    private final String salt;


    @Override
    public InputStream encrypt(char[] pass, InputStream is)
    {
        byte[] keyBytes = generatePasswordHash(pass);
        SecretKeySpec key = new SecretKeySpec(keyBytes, encodeAlgorithm);

        try {
            Cipher cipher = Cipher.getInstance(encodeAlgorithm);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return new CipherInputStream(is, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream decrypt(char[] pass, InputStream is)
    {
        byte[] keyBytes = generatePasswordHash(pass);
        SecretKeySpec key = new SecretKeySpec(keyBytes, encodeAlgorithm);

        try {
            Cipher cipher = Cipher.getInstance(encodeAlgorithm);

            cipher.init(Cipher.DECRYPT_MODE, key);

            return new CipherInputStream(is, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMd5HashFor(InputStream inputStream) throws IOException
    {
        var buffer = new byte[8192];
        int read;
        try(inputStream)
        {
            while ((read = inputStream.read(buffer)) > 0)
                messageDigest.update(buffer, 0, read);
        }
        byte[] digest = messageDigest.digest();
        return bytesToHex(digest);
    }

    @Override
    public byte[] generatePasswordHash(char[] password)
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec(password, salt.getBytes(), iterations, keyLength);
            return secretKeyFactory.generateSecret(spec)
                    .getEncoded();
        } catch (InvalidKeySpecException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes)
    {
        var builder = new StringBuilder();
        for (var b : bytes) {
            builder.append(String.format("%02x", b & 0xff));
        }
        return builder.toString();
    }
}
