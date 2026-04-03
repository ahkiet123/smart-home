package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHome(Home home);
}
