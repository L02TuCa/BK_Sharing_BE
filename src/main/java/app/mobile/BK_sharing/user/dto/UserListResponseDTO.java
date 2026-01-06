package app.mobile.BK_sharing.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponseDTO {
    private boolean success;
    private String message;
    private List<UserDto> data;
    private PaginationInfo pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private int total;
        private int page;
        private int limit;
        private int totalPages;
    }
}