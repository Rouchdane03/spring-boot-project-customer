package com.rouchFirstCode.auth;

import com.rouchFirstCode.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class LogUserController {

    private final LogUserService logUserService;

    public LogUserController(LogUserService logUserService) {
        this.logUserService = logUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserIdentifiers userIdentifiers){
        UserAuthReponse userAuthReponse = logUserService.loginUser(userIdentifiers);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, userAuthReponse.token())
                .body(userAuthReponse);
    }
}
