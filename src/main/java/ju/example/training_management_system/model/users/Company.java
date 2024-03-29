package ju.example.training_management_system.model.users;

import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company extends User {

  private String companyName;
  private String industry;
  private Integer numOfEmployees;
  private Integer establishmentYear;
  private double rating;
  private double averageRating;
  private int ratingCount;

  public Company(String email, String password) {
    super(email, password);
  }

  public Company build(Map<String, Object> properties) {
    Company company = new Company();
    company.setCompanyName(capitalizeFirstLetter((String) properties.get("companyName")));
    company.setEmail((String) properties.get("email"));
    company.setPassword((String) properties.get("password"));
    company.setJoinDate(LocalDateTime.now());
    return company;
  }

  public void addRating(double rating) {
    this.averageRating = (this.averageRating * this.ratingCount + rating) / (this.ratingCount + 1);
    this.ratingCount++;
  }
}
