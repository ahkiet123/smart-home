package fit.nlu.dapm.dto.room;

public class RoomDropdownResponse {
    private Long id;
    private String roomName;

    public RoomDropdownResponse() {
    }

    public RoomDropdownResponse(Long id, String roomName) {
        this.id = id;
        this.roomName = roomName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
