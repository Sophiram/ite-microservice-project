package kh.edu.istad.platform.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping
    Map<String, Object> getBusiness(@AuthenticationPrincipal Jwt jwt) {
        String businessId = UUID.randomUUID().toString();

        return Map.of(
                "businessId", businessId,
                "accessToken", jwt.getTokenValue(),
                "status", "ACTIVE"
        );
    }
}
