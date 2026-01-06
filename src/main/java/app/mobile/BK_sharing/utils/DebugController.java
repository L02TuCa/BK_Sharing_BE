package app.mobile.BK_sharing.utils;

import app.mobile.BK_sharing.user.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final UserRepository userRepository;

    public DebugController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/test-users")
    public List<Map<String, Object>> testUsers() {
        // Simple test without relationships
        return userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getUserId());
                    map.put("username", user.getUsername());
                    map.put("email", user.getEmail());
                    map.put("role", user.getRole().name());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/count-users")
    public Map<String, Long> countUsers() {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userRepository.count());
        return result;
    }
}
