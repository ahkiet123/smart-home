package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.user.UpdateProfileRequest;
import fit.nlu.dapm.dto.user.UserProfileResponse;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.entity.UserProfile;
import fit.nlu.dapm.exception.ResourceNotFoundException;
import fit.nlu.dapm.mapper.GenericMapper;
import fit.nlu.dapm.repository.UserProfileRepository;
import fit.nlu.dapm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

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

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

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
