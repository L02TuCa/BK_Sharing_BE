package app.mobile.BK_sharing.utils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestSecurityController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "{\"status\": \"public\", \"message\": \"No authentication needed\"}";
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "{\"status\": \"secure\", \"message\": \"Should also work without auth\"}";
    }
}