package ju.example.training_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.model.users.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TwoFactorAuthentication {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;
  private LocalDateTime persistenceTime;
  private LocalDateTime expiryTime;

  @Enumerated(EnumType.STRING)
  private TokenStatus tokenExpiryStatus;

  @ManyToOne private User user;
}
