package fit.nlu.dapm.dto.device;

import java.util.List;

public class DeviceDetailResponse {
    private Long id;
    private String deviceName;
    private String roomName;
    private Double powerRating;
    private Double hours;
    private Boolean isOn;
    private Double kwhToday;
    private Double costToday;
    private List<DeviceUsageResponse> usageLogs;

    public DeviceDetailResponse(Long id, String deviceName, String roomName, Double powerRating,
                                Double hours, Boolean isOn, Double kwhToday, Double costToday,
                                List<DeviceUsageResponse> usageLogs) {
        this.id = id;
        this.deviceName = deviceName;
        this.roomName = roomName;
        this.powerRating = powerRating;
        this.hours = hours;
        this.isOn = isOn;
        this.kwhToday = kwhToday;
        this.costToday = costToday;
        this.usageLogs = usageLogs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Double getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(Double powerRating) {
        this.powerRating = powerRating;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Boolean getIsOn() {
        return isOn;
    }

    public void setIsOn(Boolean isOn) {
        this.isOn = isOn;
    }

    public Double getKwhToday() {
        return kwhToday;
    }

    public void setKwhToday(Double kwhToday) {
        this.kwhToday = kwhToday;
    }

    public Double getCostToday() {
        return costToday;
    }

    public void setCostToday(Double costToday) {
        this.costToday = costToday;
    }

    public List<DeviceUsageResponse> getUsageLogs() {
        return usageLogs;
    }

    public void setUsageLogs(List<DeviceUsageResponse> usageLogs) {
        this.usageLogs = usageLogs;
    }
}
