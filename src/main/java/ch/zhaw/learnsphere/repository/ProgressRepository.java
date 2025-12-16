package ch.zhaw.learnsphere.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.learnsphere.model.Progress;

public interface ProgressRepository extends MongoRepository<Progress, String> {

    Optional<Progress> findByCourseIdAndStudentSub(String courseId, String studentSub);
}
