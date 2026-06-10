package com.example.securitydemo.api;

import java.util.List;

public record ApiResponse(String message, List<String> allowedFor) {
}
