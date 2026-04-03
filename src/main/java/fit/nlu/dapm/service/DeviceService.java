package fit.nlu.dapm.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceService {

    public Map<String, Object> calculateEstimate(Double power, Integer quantity, Double hours) {
        double p = (power != null) ? power : 0;
        int q = (quantity != null) ? quantity : 1;
        double h = (hours != null) ? hours : 0;
        double rate = 2500; // Đơn giá cố định để demo

        double kwhPerHour = (p * q) / 1000;
        double kwhPerDay = kwhPerHour * h;
        double kwhPerMonth = kwhPerDay * 30;
        double kwhPerYear = kwhPerMonth * 12;

        Map<String, Object> response = new HashMap<>();
        response.put("hourly", Math.round(kwhPerHour * rate));
        response.put("daily", Math.round(kwhPerDay * rate));
        response.put("monthly", Math.round(kwhPerMonth * rate));
        response.put("yearly", Math.round(kwhPerYear * rate));
        response.put("kwhMonthly", String.format("%.2f", kwhPerMonth));

        return response;
    }
}