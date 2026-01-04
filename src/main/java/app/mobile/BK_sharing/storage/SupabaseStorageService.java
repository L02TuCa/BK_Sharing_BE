package app.mobile.BK_sharing.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        // Supabase upload URL (POST to storage)
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s",
                supabaseUrl, bucketName, uniqueFilename);

        // Upload to Supabase
        RestClient restClient = RestClient.create();

        restClient.post()
                .uri(uploadUrl)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", file.getContentType())
                .body(file.getBytes())
                .retrieve()
                .toBodilessEntity();

        // IMPORTANT: Return the PUBLIC download URL
        // Format: {supabaseUrl}/storage/v1/object/public/{bucket}/{filename}
        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseUrl, bucketName, uniqueFilename);
    }

    public void deleteFile(String fileUrl) throws IOException {
        try {
            // Extract filename from the public URL
            // URL format: https://xxx.supabase.co/storage/v1/object/public/bucket/filename
            String filename = extractFilenameFromUrl(fileUrl);

            // Supabase delete URL: DELETE /storage/v1/object/bucket/filename
            String deleteUrl = String.format("%s/storage/v1/object/%s/%s",
                    supabaseUrl, bucketName, filename);

            RestClient restClient = RestClient.create();

            restClient.delete()
                    .uri(deleteUrl)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            throw new IOException("Failed to delete file from Supabase: " + e.getMessage(), e);
        }
    }

    // Helper method to extract filename from URL
    private String extractFilenameFromUrl(String fileUrl) {
        // Remove everything before the last "/" to get filename
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    public boolean fileExists(String fileUrl) {
        try {
            RestClient restClient = RestClient.create();

            restClient.get()
                    .uri(fileUrl)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
