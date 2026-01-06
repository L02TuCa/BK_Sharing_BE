package app.mobile.BK_sharing.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserDto user;
}