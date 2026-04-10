package fit.nlu.dapm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.DeviceUsageLog;

@Repository
public interface DeviceUsageLogRepository extends JpaRepository<DeviceUsageLog, Long> {
    Page<DeviceUsageLog> findByDevice(Device device, Pageable pageable);
    List<DeviceUsageLog> findByDeviceAndUsageDateBetween(Device device, LocalDate startDate, LocalDate endDate);
    // log theo ngày
    List<DeviceUsageLog> findByDeviceAndUsageDate(Device device, LocalDate date);

        List<DeviceUsageLog> findByDeviceOrderByUsageDateDescIdDesc(Device device);

        List<DeviceUsageLog> findByUsageDate(LocalDate usageDate);

        List<DeviceUsageLog> findByUsageDateBetween(LocalDate startDate, LocalDate endDate);
}
