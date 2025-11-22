package vn.ean.legia.repository.legia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ean.legia.model.legia.Staff;
import java.util.List;


@Repository("legiaStaffRepo")
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    List<Staff> findByName(String name);

    boolean existsByName(String name);

    Page<Staff> findByNameContainingIgnoreCaseOrSalary(String username, int salary, Pageable pageable);

    Page<Staff> findByNameContainingIgnoreCase(String username, Pageable pageable);
}
