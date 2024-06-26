package org.example.user.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "webpos.jwt")
public class JwtProperties {

    private Resource location;

    private String password;

    private final String alias = "webpos";

    private Duration tokenTTL = Duration.ofMinutes(30);
}
