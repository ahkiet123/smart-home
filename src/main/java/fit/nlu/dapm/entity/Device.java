package fit.nlu.dapm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devices")
public class Device extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_type_id")
    @JsonIgnore
    private DeviceType deviceType;

    @Column(name = "device_name")
    private String deviceName;

    private String brand;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "power_rating")
    private Double powerRating;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeviceUsageLog> usageLogs = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnergyRecord> energyRecords = new ArrayList<>();

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public Double getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(Double powerRating) {
        this.powerRating = powerRating;
    }

    public List<DeviceUsageLog> getUsageLogs() {
        return usageLogs;
    }

    public void setUsageLogs(List<DeviceUsageLog> usageLogs) {
        this.usageLogs = usageLogs;
    }

    public List<EnergyRecord> getEnergyRecords() {
        return energyRecords;
    }

    public void setEnergyRecords(List<EnergyRecord> energyRecords) {
        this.energyRecords = energyRecords;
    }
}
