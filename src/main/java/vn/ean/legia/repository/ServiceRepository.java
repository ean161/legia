package vn.ean.legia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.ean.legia.model.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    
}
