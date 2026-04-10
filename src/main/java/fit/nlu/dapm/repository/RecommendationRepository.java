package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByHome(Home home);
    List<Recommendation> findByHomeAndStatus(Home home, Recommendation.RecommendationStatus status);
}
