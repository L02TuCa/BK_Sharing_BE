package app.mobile.BK_sharing.utils;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping({"/api/health"})
    public Map<String, String> home() {
        // Spring Boot automatically converts this Map into a JSON object.
        return Collections.singletonMap("status", "OK");
    }
}
