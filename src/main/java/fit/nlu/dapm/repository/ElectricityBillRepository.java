package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.ElectricityBill;
import fit.nlu.dapm.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectricityBillRepository extends JpaRepository<ElectricityBill, Long> {
    List<ElectricityBill> findByHome(Home home);
    Optional<ElectricityBill> findByHomeAndMonthAndYear(Home home, Integer month, Integer year);
    Boolean existsByHomeAndMonthAndYear(Home home, Integer month, Integer year);
}
