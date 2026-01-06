package app.mobile.BK_sharing.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryListResponseDTO {
    private boolean success;
    private String message;
    private List<CategoryDTO> data;
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