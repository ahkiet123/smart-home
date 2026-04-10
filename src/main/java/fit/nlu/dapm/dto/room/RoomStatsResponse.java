package fit.nlu.dapm.dto.room;

public class RoomStatsResponse {
    private Long id;
    private Long homeId;
    private String roomName;
    private String roomType;
    private Double area;
    private Integer deviceCount;
    private Double currentKwh;
    private Double currentCost;
    private Double previousKwh;
    private Double previousCost;
    private Double trendPercent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHomeId() {
        return homeId;
    }

    public void setHomeId(Long homeId) {
        this.homeId = homeId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }

    public Double getCurrentKwh() {
        return currentKwh;
    }

    public void setCurrentKwh(Double currentKwh) {
        this.currentKwh = currentKwh;
    }

    public Double getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(Double currentCost) {
        this.currentCost = currentCost;
    }

    public Double getPreviousKwh() {
        return previousKwh;
    }

    public void setPreviousKwh(Double previousKwh) {
        this.previousKwh = previousKwh;
    }

    public Double getPreviousCost() {
        return previousCost;
    }

    public void setPreviousCost(Double previousCost) {
        this.previousCost = previousCost;
    }

    public Double getTrendPercent() {
        return trendPercent;
    }

    public void setTrendPercent(Double trendPercent) {
        this.trendPercent = trendPercent;
    }
}