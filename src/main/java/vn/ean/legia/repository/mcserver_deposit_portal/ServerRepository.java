package vn.ean.legia.repository.mcserver_deposit_portal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.ean.legia.model.User;
import vn.ean.legia.model.mcserver_deposit_portal.Server;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Page<Server> findByUserAndTitleContainingIgnoreCase(User user, String title, Pageable pageable);

    Page<Server> findByUser(User user, Pageable pageable);

    boolean existsByTitle(String title);
}
