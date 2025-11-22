package vn.ean.legia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.ean.legia.model.ServiceCategory;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    
}
