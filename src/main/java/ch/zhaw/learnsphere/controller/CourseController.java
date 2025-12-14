package ch.zhaw.learnsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.learnsphere.model.Course;
import ch.zhaw.learnsphere.model.CourseCreateDTO;
import ch.zhaw.learnsphere.model.CourseUpdateDTO;
import ch.zhaw.learnsphere.repository.CourseRepository;


@RestController
@RequestMapping("/api/teacher")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody CourseCreateDTO dto) {
        // temporär: teacherSub fix (JWT kommt später)
        Course course = new Course(
                dto.getTitle(),
                dto.getDescription(),
                "auth0|testteacher"
        );
        Course saved = courseRepository.save(course);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses() {
        String teacherSub = "auth0|testteacher"; // temporär
        return ResponseEntity.ok(
                courseRepository.findByTeacherSub(teacherSub)
        );
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable String courseId) {
        return courseRepository.findById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String courseId,
            @RequestBody CourseUpdateDTO dto) {

        return courseRepository.findById(courseId)
                .map(course -> {
                    course.setTitle(dto.getTitle());
                    course.setDescription(dto.getDescription());
                    return ResponseEntity.ok(courseRepository.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
