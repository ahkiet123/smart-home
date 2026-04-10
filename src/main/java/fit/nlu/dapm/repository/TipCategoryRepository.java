package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.TipCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipCategoryRepository extends JpaRepository<TipCategory, Long> {
    Optional<TipCategory> findByCategoryName(String categoryName);
}
