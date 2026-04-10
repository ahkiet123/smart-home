package fit.nlu.dapm.dto.device;

public class DeviceResponse {
    private Long id;
    private String deviceName;
    private String roomName;
    private Long roomId;
    private Long deviceTypeId;
    private Double powerRating;
    private String brand;
    private String modelNumber;

    public DeviceResponse(Long id, String deviceName, String roomName, Long roomId,
                          Long deviceTypeId, Double powerRating, String brand, String modelNumber) {
        this.id = id;
        this.deviceName = deviceName;
        this.roomName = roomName;
        this.roomId = roomId;
        this.deviceTypeId = deviceTypeId;
        this.powerRating = powerRating;
        this.brand = brand;
        this.modelNumber = modelNumber;
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

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Long deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public Double getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(Double powerRating) {
        this.powerRating = powerRating;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }
}
