package ju.example.training_management_system.repository;

import ju.example.training_management_system.model.company.advertisement.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    boolean existsByJobTitle(String jobTitle);

}