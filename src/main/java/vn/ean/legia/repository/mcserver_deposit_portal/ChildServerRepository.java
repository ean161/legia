package vn.ean.legia.repository.mcserver_deposit_portal;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ean.legia.model.mcserver_deposit_portal.ChildServer;

public interface ChildServerRepository extends JpaRepository<ChildServer, Long> {
    
}
