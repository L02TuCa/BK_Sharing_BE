package app.mobile.BK_sharing.course;

import app.mobile.BK_sharing.course.dto.CourseRequest;
import app.mobile.BK_sharing.user.User;
import app.mobile.BK_sharing.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Course createCourse(CourseRequest request) {
        // Find the user who created the course
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + request.getCreatedById()));

        // Check if course code already exists
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + request.getCourseCode());
        }

        // Create and save the course
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .description(request.getDescription())
                .createdBy(createdBy)
                .build();

        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Course getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with code: " + courseCode));
    }

    public Course updateCourse(Long id, CourseRequest request) {
        Course course = getCourseById(id);

        // Check if course code is being changed and if it already exists
        if (!course.getCourseCode().equals(request.getCourseCode()) &&
                courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + request.getCourseCode());
        }

        // Update fields
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public boolean existsByCourseCode(String courseCode) {
        return courseRepository.existsByCourseCode(courseCode);
    }
}