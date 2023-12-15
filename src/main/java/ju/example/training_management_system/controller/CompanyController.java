package ju.example.training_management_system.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.service.company.CompanyService;
import ju.example.training_management_system.service.company.post.AdsPostService;
import ju.example.training_management_system.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static ju.example.training_management_system.util.Utils.getRequiredDashboard;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final AdsPostService adsPostService;
    private final HttpServletResponse response;
    private final HttpServletRequest request;

    @GetMapping("/dashboard")
    public String setUpCompanyDashboard() {

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            Cookie companyNameCookie = new Cookie("companyName", companyName);
            companyNameCookie.setPath("/");
            companyNameCookie.setMaxAge(24 * 60 * 60 * 30);
            response.addCookie(companyNameCookie);
        }

        session.setAttribute("email", email);

        return "company-dashboard";
    }

    @GetMapping("/manage-profile")
    public String getManageProfilePage() {
        return "manage-profile";
    }

    @PutMapping("/manage-profile")
    public String manageProfile(@RequestBody Map<String, Object> userData) {
        System.out.println(userData.entrySet());
        companyService.updateCompanyDetails(userData);
        return null;
    }

    @GetMapping("/job/post")
    public String getPostAdsForm(Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email != null) {
            model.addAttribute("email", email);
            String companyName = companyService.getCompanyName(email);
            model.addAttribute("companyName", companyName);
        }
        return "job-post";
    }

    @PostMapping("/job/post")
    public ResponseEntity<?> postAds(@ModelAttribute AdvertisementDto postDto, Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (email != null) {
            ApiResponse apiResponse = adsPostService.postAd(postDto, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse.getMessage());

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }

    @GetMapping("/get/ads")
    public ResponseEntity<?> getAdvertisementsForCompany() {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            List<Advertisement> advertisements = adsPostService.getAllAdvertisementsForCompany(companyName);
            session.setAttribute("email", email);
            return ResponseEntity.ok(advertisements);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping("/delete/ad/{position}")
    public void deleteAd(@PathVariable("position") String position, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            adsPostService.deleteAd(companyName, position);
        }
    }

    @PutMapping("/update/ad")
    public ResponseEntity<?> updateAd(@ModelAttribute AdvertisementDto adDto) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (email != null) {
            ApiResponse apiResponse = adsPostService.updateAd(adDto, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }
}