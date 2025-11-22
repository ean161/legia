package vn.ean.legia.repository.sports_store;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ean.legia.model.User;
import vn.ean.legia.model.sports_store.Deploy;

import org.springframework.data.domain.Pageable;

@Repository("sportsStoreDeployRepository")
public interface DeployRepository extends JpaRepository<Deploy, Long> {
    Page<Deploy> findByUser(User user, Pageable pageable);
}
