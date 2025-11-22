package vn.ean.legia.repository.legia;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ean.legia.model.legia.Staff;
import vn.ean.legia.model.legia.StaticMoney;

@Repository("legiaStaticMoneyRepo")
public interface StaticMoneyRepository extends JpaRepository<StaticMoney, Long> {
    
    List<StaticMoney> findByStaffAndCreatedDateGreaterThanAndCreatedDateLessThan(Staff staff, Long start, Long end);

    Page<StaticMoney> findByStaff_NameContainingIgnoreCase(String name, Pageable pageable);
}
