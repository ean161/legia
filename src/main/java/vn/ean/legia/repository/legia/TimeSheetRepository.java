package vn.ean.legia.repository.legia;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.ean.legia.model.legia.Staff;
import vn.ean.legia.model.legia.TimeSheet;

@Repository("legiaTimeSheetRepo")
public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {
    
    TimeSheet findByStaffAndEnd(Staff staff, int end);

    List<TimeSheet> findByEnd(Long end);

    List<TimeSheet> findListByStaffAndEnd(Staff staff, int end);

    Page<TimeSheet> findByStaff_NameContainingIgnoreCase(String name, Pageable pageable);

    List<TimeSheet> findByStaffAndStartGreaterThanAndEndLessThanAndEndGreaterThan(Staff staff, Long start, Long end, Long endZero);

    @Transactional
    @Modifying
    @Query("UPDATE TimeSheet t SET t.end = :end, t.worktime = :normal, t.overtime = :ot WHERE t.id = :id")
    int updateEndByStaffId(@Param("end") Long end, @Param("normal") Long normal, @Param("ot") Long ot,  @Param("id") Long id);
}
