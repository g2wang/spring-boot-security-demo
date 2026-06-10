package com.example.securitydemo.api;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/hello")
    public ApiResponse publicHello() {
        return new ApiResponse("Hello from a public endpoint.", List.of("anonymous"));
    }

    @GetMapping("/profile")
    public ApiResponse profile(Principal principal) {
        return new ApiResponse("Hello, " + principal.getName() + ".", List.of("authenticated"));
    }

    @GetMapping("/reports/user")
    public ApiResponse userReport() {
        return new ApiResponse("User report data.", List.of("ROLE_USER", "ROLE_ADMIN"));
    }

    @GetMapping("/reports/admin")
    public ApiResponse adminReport() {
        return new ApiResponse("Admin report data.", List.of("ROLE_ADMIN"));
    }
}
