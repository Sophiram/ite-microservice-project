package kh.edu.istad.platform.business;

import lombok.RequiredArgsConstructor;
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
    Map<String, Object> getBusiness() {

        String businessId = UUID.randomUUID().toString();

        return Map.of(
                "businessId", businessId,
                "businessName", "ISTAD Business",
                "status", "ACTIVE"
        );
    }
}