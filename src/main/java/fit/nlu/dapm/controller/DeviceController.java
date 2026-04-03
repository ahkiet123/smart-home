package fit.nlu.dapm.controller;

import fit.nlu.dapm.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/estimate")
    public ResponseEntity<?> getEstimate(
            @RequestParam Double power,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam Double hours) {
        return ResponseEntity.ok(deviceService.calculateEstimate(power, quantity, hours));
    }
}