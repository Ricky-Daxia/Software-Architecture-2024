package org.example.gateway.Utils;

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


    public Long parseToken(String token) throws Exception {
        if (token == null) {
            throw new Exception("haven't logged in");
        }

        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new Exception("invalid token");
        }

        if (!jwt.verify()) {
            throw new Exception("invalid token");
        }

        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new Exception("expired");
        }

        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            throw new Exception("invalid token");
        }

        try {
            return Long.parseLong(userPayload.toString());
        } catch (RuntimeException e) {
           throw new Exception("invalid token");
        }
    }

}
