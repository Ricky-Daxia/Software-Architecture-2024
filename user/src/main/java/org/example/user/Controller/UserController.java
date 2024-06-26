package org.example.user.Controller;

import org.example.user.Utils.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class UserController {

    private final JwtTool jwtTool;

    @Autowired
    public UserController(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }

    @GetMapping(path = "/user/login/{userId}")
    public Mono<String> userLogin(@PathVariable long userId) {
        String token = jwtTool.createToken(userId, Duration.ofMinutes(30));
        return Mono.just(token);
    }
}
