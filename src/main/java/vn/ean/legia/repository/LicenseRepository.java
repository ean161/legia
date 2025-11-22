package vn.ean.legia.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import vn.ean.legia.model.License;
import vn.ean.legia.model.User;

public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findByUser(User user);

    List<License> findByUserAndExpiredDateGreaterThan(User user, Long expiredDate);
    
    License findByUserAndDirectory(User user, String directory);

    Page<License> findByUserOrDirectoryContainingIgnoreCase(User user, String directory, Pageable pageable);

    Page<License> findByDirectoryContainingIgnoreCase(String directory, Pageable pageable);

    List<License> findByUserAndExpiredDateGreaterThanOrderByTitleAsc(User user, Long expiredDate);

    List<License> findByUserAndDirectoryAndExpiredDateGreaterThanOrderByTitleAsc(User user, String directory, Long expiredDate);

    @Transactional
    @Modifying
    @Query("UPDATE License l SET l.expiredDate = :expiredDate WHERE l.id = :id")
    int updateExpiredDateById(@Param("expiredDate") Long expiredDate, @Param("id") Long id);
}
