package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByHome(Home home);
}
