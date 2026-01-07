package app.mobile.BK_sharing.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find by course code (unique)
    Optional<Course> findByCourseCode(String courseCode);

    // Find by course name (contains, case-insensitive)
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    // Find by course code (contains, case-insensitive)
    List<Course> findByCourseCodeContainingIgnoreCase(String courseCode);

    // Find courses created by a specific user
    List<Course> findByCreatedBy_UserId(Long userId);

    // Custom query: Find courses with document count
    @Query("SELECT c, COUNT(d) as documentCount FROM Course c " +
            "LEFT JOIN c.documents d " +
            "GROUP BY c.courseId " +
            "ORDER BY documentCount DESC")
    List<Object[]> findCoursesWithDocumentCount();

    // Check if course code exists
    boolean existsByCourseCode(String courseCode);

}