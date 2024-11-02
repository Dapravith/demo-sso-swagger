package com.example.demossoswagger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/admin")
    public String SecureAdminEndpoint() {
        return "Hello Admin";
    }

    @GetMapping("/user")
    public String SecureUserEndpoint() {
        return "Hello User";
    }

    @GetMapping("/developer")
    public String SecureDeveloperEndpoint() {
        return "Hello Developer";
    }


}
