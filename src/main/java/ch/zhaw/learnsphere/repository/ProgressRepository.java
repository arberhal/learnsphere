package ch.zhaw.learnsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.learnsphere.model.Progress;

public interface ProgressRepository extends MongoRepository<Progress, String> {
    
    Optional<Progress> findByCourseIdAndStudentSub(String courseId, String studentSub);
    
    // NEW: Find all progress records for a student
    List<Progress> findByStudentSub(String studentSub);
}