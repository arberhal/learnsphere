package ch.zhaw.learnsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.learnsphere.model.Progress;
import ch.zhaw.learnsphere.model.ProgressStatus;

public interface ProgressRepository extends MongoRepository<Progress, String> {

    Optional<Progress> findByCourseIdAndStudentSub(String courseId, String studentSub);
    

    List<Progress> findByStudentSub(String studentSub);
    

    List<Progress> findByStudentSubAndStatus(String studentSub, ProgressStatus status);
    
 
    List<Progress> findByStatus(ProgressStatus status);
    

    long countByStudentSubAndStatus(String studentSub, ProgressStatus status);
}
