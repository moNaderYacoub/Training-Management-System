package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.AdminService.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String setUpAdminDashboard(Model model){
        adminService.setUpAdminDashboardPage(model);
        return "/admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String setUpAdminUserList(Model model){
        adminService.setUpUserListPage(model);
        return "/admin/users";
    }

    @GetMapping("/advertisements")
    public String setUpAdminAdvertisementList(){
        return "/admin/advertisement-data";
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId){
        ApiResponse response = adminService.deleteUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }
}