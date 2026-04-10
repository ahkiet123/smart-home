package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.user.UpdateProfileRequest;
import fit.nlu.dapm.dto.user.UserProfileResponse;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.entity.UserProfile;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.exception.ResourceNotFoundException;
import fit.nlu.dapm.mapper.GenericMapper;
import fit.nlu.dapm.repository.UserProfileRepository;
import fit.nlu.dapm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final String PHONE_REGEX = "^(0|\\+84)(3|5|7|8|9)\\d{8}$";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private GenericMapper mapper;

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = user.getProfile();
        UserProfileResponse response = new UserProfileResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        if (profile != null) {
            response.setAddress(profile.getAddress());
            response.setCity(profile.getCity());
            response.setCountry(profile.getCountry());
        }

        return response;
    }

    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String nextFullName = request.getFullName() == null ? "" : request.getFullName().trim();
        String nextPhone = request.getPhone() == null ? "" : request.getPhone().trim();

        if (nextFullName.isBlank()) {
            throw new BadRequestException("Họ và tên không được để trống");
        }

        if (!nextPhone.isBlank() && !nextPhone.matches(PHONE_REGEX)) {
            throw new BadRequestException("Số điện thoại không hợp lệ (định dạng VN)");
        }

        String currentFullName = user.getFullName() == null ? "" : user.getFullName().trim();
        String currentPhone = user.getPhone() == null ? "" : user.getPhone().trim();

        boolean fullNameUnchanged = currentFullName.equals(nextFullName);
        boolean phoneUnchanged = currentPhone.equals(nextPhone);
        if (fullNameUnchanged && phoneUnchanged) {
            throw new BadRequestException("Thông tin chưa thay đổi");
        }

        if (!nextPhone.isBlank()) {
            boolean phoneUsedByAnotherUser = Boolean.TRUE.equals(
                    userRepository.existsByPhoneAndIdNot(nextPhone, userId)
            );
            if (phoneUsedByAnotherUser) {
                throw new BadRequestException("Số điện thoại đã được sử dụng");
            }
        }

        user.setFullName(nextFullName);
        user.setPhone(nextPhone.isBlank() ? null : nextPhone);

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setCountry(request.getCountry());

        userProfileRepository.save(profile);
        userRepository.save(user);
    }
}
