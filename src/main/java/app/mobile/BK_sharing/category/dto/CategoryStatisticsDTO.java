package app.mobile.BK_sharing.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatisticsDTO {

    private Long totalCategories;
    private Long totalDocuments;
    private Map<String, Long> documentsPerCategory;
    private CategoryDTO mostPopularCategory;
}