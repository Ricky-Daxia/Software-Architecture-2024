package org.example.user.Utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

/*
https://github.com/DptionC/hmall-springcloud/blob/master/hm-service/src/main/java/com/hmall/utils/JwtTool.java
*/
@Component
public class JwtTool {

    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("ES256", keyPair);
    }

    public String createToken(long userId, Duration ttl) {
        return JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date((System.currentTimeMillis() + ttl.toMillis())))
                .setSigner(jwtSigner)
                .sign();
    }

}
