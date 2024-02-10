package com.barabanov.backup;

import com.barabanov.backup.ui.SecretHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class SwingAppConfig
{

    @Bean
    public SecretHolder secretHolder()
    {
        return new SecretHolder();
    }

    @Bean
    public Supplier<char[]> passSupplier()
    {
        return () -> secretHolder().getPass();
    }
}
