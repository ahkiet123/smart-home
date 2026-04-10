package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Tip;
import fit.nlu.dapm.entity.TipCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipRepository extends JpaRepository<Tip, Long> {
    List<Tip> findByCategory(TipCategory category);
}
