package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.DeviceUsageLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceUsageLogRepository extends JpaRepository<DeviceUsageLog, Long> {
    Page<DeviceUsageLog> findByDevice(Device device, Pageable pageable);
    List<DeviceUsageLog> findByDeviceAndUsageDateBetween(Device device, LocalDate startDate, LocalDate endDate);
    Optional<DeviceUsageLog> findByDeviceAndUsageDate(Device device, LocalDate usageDate);
}
