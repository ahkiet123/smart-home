package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.EnergyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {
    List<EnergyRecord> findByDeviceAndUsageDateBetween(Device device, LocalDate startDate, LocalDate endDate);
    Optional<EnergyRecord> findByDeviceAndUsageDate(Device device, LocalDate usageDate);

    @Query("""
        SELECT er.usageDate, COALESCE(SUM(er.energyConsumed), 0)
        FROM EnergyRecord er
        JOIN er.device d
        JOIN d.room r
        JOIN r.home h
        WHERE h.user.id = :userId
          AND er.usageDate BETWEEN :fromDate AND :toDate
          AND (:roomId IS NULL OR r.id = :roomId)
          AND (:deviceId IS NULL OR d.id = :deviceId)
        GROUP BY er.usageDate
        ORDER BY er.usageDate
        """)
    List<Object[]> aggregateDailyEnergy(
        @Param("userId") Long userId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("roomId") Long roomId,
        @Param("deviceId") Long deviceId
    );

    @Query("""
        SELECT COALESCE(SUM(er.energyConsumed), 0)
        FROM EnergyRecord er
        JOIN er.device d
        JOIN d.room r
        JOIN r.home h
        WHERE h.user.id = :userId
          AND er.usageDate = :usageDate
          AND (:roomId IS NULL OR r.id = :roomId)
          AND (:deviceId IS NULL OR d.id = :deviceId)
        """)
    Double aggregateTotalEnergyByDate(
        @Param("userId") Long userId,
        @Param("usageDate") LocalDate usageDate,
        @Param("roomId") Long roomId,
        @Param("deviceId") Long deviceId
    );

    @Query("""
        SELECT r.id, r.roomName, COALESCE(SUM(er.energyConsumed), 0)
        FROM EnergyRecord er
        JOIN er.device d
        JOIN d.room r
        JOIN r.home h
        WHERE h.user.id = :userId
          AND er.usageDate BETWEEN :fromDate AND :toDate
          AND (:roomId IS NULL OR r.id = :roomId)
          AND (:deviceId IS NULL OR d.id = :deviceId)
        GROUP BY r.id, r.roomName
        ORDER BY COALESCE(SUM(er.energyConsumed), 0) DESC
        """)
    List<Object[]> aggregateByRoom(
        @Param("userId") Long userId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("roomId") Long roomId,
        @Param("deviceId") Long deviceId
    );

    @Query("""
        SELECT d.id, d.deviceName, COALESCE(SUM(er.energyConsumed), 0)
        FROM EnergyRecord er
        JOIN er.device d
        JOIN d.room r
        JOIN r.home h
        WHERE h.user.id = :userId
          AND er.usageDate BETWEEN :fromDate AND :toDate
          AND (:roomId IS NULL OR r.id = :roomId)
          AND (:deviceId IS NULL OR d.id = :deviceId)
        GROUP BY d.id, d.deviceName
        ORDER BY COALESCE(SUM(er.energyConsumed), 0) DESC
        """)
    List<Object[]> aggregateByDevice(
        @Param("userId") Long userId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("roomId") Long roomId,
        @Param("deviceId") Long deviceId
    );

    @Query("""
        SELECT r.id, r.roomName, COALESCE(SUM(er.energyConsumed), 0)
        FROM EnergyRecord er
        JOIN er.device d
        JOIN d.room r
        JOIN r.home h
        WHERE h.user.id = :userId
          AND er.usageDate = :usageDate
        GROUP BY r.id, r.roomName
        ORDER BY COALESCE(SUM(er.energyConsumed), 0) DESC
        """)
    List<Object[]> aggregateRoomsByDate(
        @Param("userId") Long userId,
        @Param("usageDate") LocalDate usageDate
    );

    @Query("""
        SELECT d.id, d.deviceName, d.powerRating, COALESCE(SUM(er.energyConsumed), 0)
        FROM EnergyRecord er
        JOIN er.device d
        JOIN d.room r
        JOIN r.home h
        WHERE h.user.id = :userId
          AND r.id = :roomId
          AND er.usageDate = :usageDate
        GROUP BY d.id, d.deviceName, d.powerRating
        ORDER BY COALESCE(SUM(er.energyConsumed), 0) DESC
        """)
    List<Object[]> aggregateDevicesByRoomAndDate(
        @Param("userId") Long userId,
        @Param("roomId") Long roomId,
        @Param("usageDate") LocalDate usageDate
    );
}
