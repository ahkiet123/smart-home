package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByRoom(Room room);
}
