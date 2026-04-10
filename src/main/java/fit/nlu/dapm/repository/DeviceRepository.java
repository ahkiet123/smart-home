package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByRoom(Room room);
    List<Device> findAllByOrderByIdAsc();

    @Query("""
            SELECT d
            FROM Device d
            JOIN d.room r
            JOIN r.home h
            WHERE h.user.id = :userId
            """)
    List<Device> findAllByUserId(@Param("userId") Long userId);
}
