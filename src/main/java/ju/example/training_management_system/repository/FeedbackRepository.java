package ju.example.training_management_system.repository;

import java.util.List;
import java.util.Optional;
import ju.example.training_management_system.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  List<Feedback> findByStudent_Id(Long studentId);

  Optional<Feedback> findTopByOrderByPostDateDesc();
}
