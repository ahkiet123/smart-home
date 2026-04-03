package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.home.HomeRequest;
import fit.nlu.dapm.dto.home.HomeResponse;
import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.exception.ResourceNotFoundException;
import fit.nlu.dapm.mapper.GenericMapper;
import fit.nlu.dapm.repository.HomeRepository;
import fit.nlu.dapm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenericMapper mapper;

    public HomeResponse createHome(Long userId, HomeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Home home = mapper.map(request, Home.class);
        home.setUser(user);

        home = homeRepository.save(home);
        return mapper.map(home, HomeResponse.class);
    }

    public Page<HomeResponse> getHomes(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Home> homes = homeRepository.findByUser(user, pageable);
        return homes.map(home -> mapper.map(home, HomeResponse.class));
    }

    public HomeResponse getHome(Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        return mapper.map(home, HomeResponse.class);
    }

    @Transactional
    public HomeResponse updateHome(Long homeId, HomeRequest request) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        mapper.mapTo(request, home);
        home = homeRepository.save(home);
        return mapper.map(home, HomeResponse.class);
    }

    @Transactional
    public void deleteHome(Long homeId) {
        if (!homeRepository.existsById(homeId)) {
            throw new ResourceNotFoundException("Home not found");
        }
        homeRepository.deleteById(homeId);
    }
}
