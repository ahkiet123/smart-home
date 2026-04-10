package fit.nlu.dapm.dto.device;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class DeviceUsageRequest {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(value = 0, message = "Hours used must be at least 0")
    @Max(value = 24, message = "Hours used cannot exceed 24")
    private Double hoursUsed;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getHoursUsed() {
        return hoursUsed;
    }

    public void setHoursUsed(Double hoursUsed) {
        this.hoursUsed = hoursUsed;
    }
}
