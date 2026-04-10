package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.EnergyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyRecordRepository extends JpaRepository<EnergyRecord, Long> {
    List<EnergyRecord> findByDeviceAndUsageDateBetween(Device device, LocalDate startDate, LocalDate endDate);
    Optional<EnergyRecord> findByDeviceAndUsageDate(Device device, LocalDate usageDate);
}
