package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
    Page<Home> findByUser(User user, Pageable pageable);
    List<Home> findByUser(User user);
}
