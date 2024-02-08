package com.barabanov.backup.cryptography;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public interface CryptoService
{
    InputStream encrypt(char[] keyStr, InputStream is);

    InputStream decrypt(char[] keyStr, InputStream is);

    String getMd5HashFor(InputStream inputStream) throws IOException;

    byte[] generatePasswordHash(char[] password);

    static void erase(char[] password)
    {
        Arrays.fill(password, (char) 0);
    }

    static void erase(byte[] password)
    {
        Arrays.fill(password, (byte) 0);
    }
}
