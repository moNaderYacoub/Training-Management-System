package ju.example.training_management_system.service;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.exception.UserAlreadyExistException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.model.users.UserFactory;
import ju.example.training_management_system.repository.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    @Transactional
    public ApiResponse registerUser(Map<String, Object> userData) {
        String roleStr = (String) userData.get("role");
        Role role = Role.toRole(roleStr);
        User user = UserFactory.createUser(role, userData);
        String hashedPassword = PasswordHashingUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyExistException("user email already exists");
            }
            user.setRole(role);
            userRepository.save(user);
            return new ApiResponse("user has been saved successfully", HttpStatus.OK);
        } catch (UserAlreadyExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
