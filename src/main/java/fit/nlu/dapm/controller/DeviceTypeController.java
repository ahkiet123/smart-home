package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.device.DeviceTypeResponse;
import fit.nlu.dapm.repository.DeviceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/device-types")
@RequiredArgsConstructor
public class DeviceTypeController {

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @GetMapping
    public List<DeviceTypeResponse> getAll() {
        return deviceTypeRepository.findAll()
                .stream()
                .map(t -> new DeviceTypeResponse(t.getId(), t.getTypeName()))
                .toList();
    }
}
