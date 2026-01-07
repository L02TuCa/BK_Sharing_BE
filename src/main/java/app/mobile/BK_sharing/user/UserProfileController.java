package app.mobile.BK_sharing.user;

import app.mobile.BK_sharing.storage.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final SupabaseStorageService storageService;

    @PostMapping("/{userId}/profile-picture")
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<ProfilePictureResponse> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        // Upload to Supabase
        String imageUrl = storageService.uploadFile(file);

        // Update user profile in database
        User user = userService.updateProfilePicture(userId, imageUrl);

        return ResponseEntity.ok(new ProfilePictureResponse(
                user.getProfilePicture(),
                "Profile picture uploaded successfully"
        ));
    }

    @DeleteMapping("/{userId}/profile-picture")
//    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<Void> deleteProfilePicture(@PathVariable Long userId) {
        userService.deleteProfilePicture(userId);
        return ResponseEntity.noContent().build();
    }

    // Response DTO
    public record ProfilePictureResponse(String imageUrl, String message) {}
}