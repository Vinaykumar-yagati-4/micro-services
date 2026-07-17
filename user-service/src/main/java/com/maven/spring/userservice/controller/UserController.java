package com.maven.spring.userservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
