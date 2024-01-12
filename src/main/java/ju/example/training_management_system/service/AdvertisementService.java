package ju.example.training_management_system.service;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.exception.AdAlreadyExistsException;
import ju.example.training_management_system.exception.AdDoesNotExistException;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.company.advertisement.Notification;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;

import static ju.example.training_management_system.model.PostStatus.PENDING;
import static ju.example.training_management_system.util.Utils.*;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public Company isUserAuthorizedAsCompany(User user, String email) throws UnauthorizedCompanyAccessException {
        if (!(user instanceof Company company)) {
            throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
        }
        return company;
    }

    public ApiResponse setUpPostAdsPage(Model model, String email) {
        try {
            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            Company company = isUserAuthorizedAsCompany(existingUser, email);

            String base64Image = null;
            if (company.getImage() != null) {
                byte[] decompressedImage = decompressImage(company.getImage());
                base64Image = convertToBase64(decompressedImage);
            }

            List<Notification> notifications = notificationRepository.findByUser(company);

            model.addAttribute("companyImage", base64Image);
            model.addAttribute("companyName", company.getCompanyName());
            model.addAttribute("notifications", notifications);

            return new ApiResponse("Set up was correctly done", HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public ApiResponse postAd(AdvertisementDto adDto, String email) {
        try {
            Advertisement ad = new Advertisement().toEntity(adDto);

            byte[] imageBytes = saveImage(adDto.getJobImage());
            ad.setImage(imageBytes);

            User user = userRepository.findByEmail(email);
            if (!(user instanceof Company company)) {
                throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
            }

            ad.setCompany(company);
            if (advertisementRepository.existsByJobTitleAndCompany(ad.getJobTitle(), company)) {
                throw new AdAlreadyExistsException("A post with the same title already exists!");
            }

            advertisementRepository.save(ad);
            return new ApiResponse("Advertisement with job title [" + ad.getJobTitle() + "] was posted successfully", HttpStatus.CREATED);
        } catch (AdAlreadyExistsException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public ApiResponse updateAd(AdvertisementDto adDto, String email) {
        try {
            Advertisement existingAd = advertisementRepository.findById(adDto.getId())
                    .orElseThrow(() -> new AdDoesNotExistException("Advertisement not found"));

            User user = userRepository.findByEmail(email);
            if (!(user instanceof Company company)) {
                throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
            }
            existingAd.setCompany(company);

            if (!Objects.equals(adDto.getJobTitle(), existingAd.getJobTitle()) &&
                    advertisementRepository.existsByJobTitleAndCompany(adDto.getJobTitle(), company)) {
                throw new AdAlreadyExistsException("An advertisement with the same title already exists!");
            }

            byte[] imageBytes = saveImage(adDto.getJobImage());

            existingAd.setImage(imageBytes);
            existingAd.setJobTitle(capitalizeFirstLetter(adDto.getJobTitle()));
            existingAd.setInternsRequired(adDto.getInternsRequired());
            existingAd.setJobDuration(adDto.getJobDuration());
            existingAd.setDescription(adDto.getDescription());
            existingAd.setJobType(adDto.getJobType());
            existingAd.setCountry(adDto.getCountry());
            existingAd.setCity(adDto.getCity());
            existingAd.setWorkMode(adDto.getWorkMode());
            existingAd.setApplicationLink(adDto.getApplicationLink());
            existingAd.setPostStatus(PENDING);

            advertisementRepository.save(existingAd); // updated advertisement
            return new ApiResponse("Advertisement with job title [" + existingAd.getJobTitle() + "] was updated successfully", HttpStatus.CREATED);
        } catch (AdAlreadyExistsException | AdDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public ApiResponse deleteAd(long adId, String email) {
        try {
            Advertisement advertisement = advertisementRepository.findById(adId)
                    .orElseThrow(() -> new AdDoesNotExistException("Advertisement with id [" + adId + "] does not exist"));

            User user = userRepository.findByEmail(email);
            if (!(user instanceof Company)) {
                throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
            }

            advertisementRepository.deleteById(adId);
            return new ApiResponse("Advertisement with job title [" + advertisement.getJobTitle() + "] was delete successfully", HttpStatus.OK);
        } catch (AdDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}