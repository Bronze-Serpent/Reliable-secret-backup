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
    // TODO: 24.01.2024 определиться с ключами для контейнеров и создавать cipher бинами, чтобы переиспользовать их.
    //  Хотя если ключи разные - переиспользовать не выйдет


    @Value("${crypto.hash.algorithm:MD5}")
    private final String hashAlgorithm;

    @Value("${crypto.encode.algorithm:Blowfish}")
    private final String encodeAlgorithm;


    @Override
    public InputStream encrypt(char[] password, InputStream is)
    {
        byte[] keyBytes = generatePasswordHash(password);
        SecretKeySpec key = new SecretKeySpec(keyBytes, encodeAlgorithm);

        try {
            Cipher cipher = Cipher.getInstance("Blowfish");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return new CipherInputStream(is, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream decrypt(char[] keyStr, InputStream is) {
        return null;
    }

    @Override
    public String getMd5HashFor(InputStream inputStream) throws IOException
    {
        try
        {
            var md = MessageDigest.getInstance(hashAlgorithm);
            var buffer = new byte[8192];
            int read;
            try(inputStream)
            {
                while ((read = inputStream.read(buffer)) > 0)
                    md.update(buffer, 0, read);
            }
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] generatePasswordHash(char[] password)
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec(password);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e)
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
