package jav.dev.pro.aiollamadepspeek.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
@Getter
@Setter
public class PropertiesConfig {

    private String username;
    private String token;
}
