package ch.zhaw.learnsphere.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.learnsphere.model.Course;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByTeacherSub(String teacherSub);
}
