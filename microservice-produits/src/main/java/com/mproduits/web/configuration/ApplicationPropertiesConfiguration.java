package com.mproduits.web.configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import org.springframework.cloud.context.config.annotation.RefreshScope;

@Component

@ConfigurationProperties("mes-configs")

@RefreshScope

public class ApplicationPropertiesConfiguration
{
    private int limitDeProduits;

    public int getLimitDeProduits()
    {
        return limitDeProduits;
    }

    public void setLimitDeProduits(int limitDeProduits)
    {
        this.limitDeProduits = limitDeProduits;
    }
}