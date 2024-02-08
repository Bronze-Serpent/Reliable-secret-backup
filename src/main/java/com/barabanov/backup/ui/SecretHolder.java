package com.barabanov.backup.ui;

import com.barabanov.backup.cryptography.CryptoService;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SecretHolder
{
    private char[] pass;

    private void removeData()
    {
        CryptoService.erase(pass);
    }
}
